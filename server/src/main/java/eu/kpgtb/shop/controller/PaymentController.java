package eu.kpgtb.shop.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import eu.kpgtb.shop.auth.User;
import eu.kpgtb.shop.config.Properties;
import eu.kpgtb.shop.data.entity.order.OrderEntity;
import eu.kpgtb.shop.data.entity.order.OrderProductEntity;
import eu.kpgtb.shop.data.entity.order.OrderProductField;
import eu.kpgtb.shop.data.entity.product.ProductEntity;
import eu.kpgtb.shop.data.entity.UserEntity;
import eu.kpgtb.shop.data.entity.product.ProductField;
import eu.kpgtb.shop.data.repository.order.OrderProductFieldRepository;
import eu.kpgtb.shop.data.repository.order.OrderProductRepository;
import eu.kpgtb.shop.data.repository.order.OrderRepository;
import eu.kpgtb.shop.data.repository.product.ProductFieldRepository;
import eu.kpgtb.shop.data.repository.product.ProductRepository;
import eu.kpgtb.shop.util.JsonResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderProductRepository orderProductRepository;
    @Autowired
    private OrderProductFieldRepository orderProductFieldRepository;
    @Autowired
    private ProductFieldRepository productFieldRepository;
    @Autowired
    private OrderRepository orderRepository;

    private final Properties properties;
    private final String SUCCESS_URL;
    private final String FAIL_URL;

    public PaymentController(Properties properties) {
        this.properties = properties;
        SUCCESS_URL = properties.getFrontendUrl() + "/payment?success";
        FAIL_URL = properties.getFrontendUrl() + "/payment?fail";
    }

    @PostMapping
    public ResponseEntity<Void> pay(@RequestBody MultiValueMap<String,String> body, Authentication authentication) throws URISyntaxException, StripeException {
        List<SessionCreateParams.LineItem> items = new ArrayList<>();
        List<OrderProductEntity> opEntities = new ArrayList<>();
        List<SessionCreateParams.CustomField> customFields = new ArrayList<>();

        Map<String,String> products = body.toSingleValueMap();
        products.forEach((pIdStr, quantityStr) -> {
            int pId = Integer.parseInt(pIdStr);
            int quantity = Integer.parseInt(quantityStr);

            if(quantity < 1) return;
            Optional<ProductEntity> entityOpt = productRepository.findById(pId);
            if(entityOpt.isEmpty()) return;
            ProductEntity entity = entityOpt.get();

            try {
                Product product = Product.retrieve(entity.getStripeId());
                opEntities.add(new OrderProductEntity(entity, quantity, new ArrayList<>()));
                items.add(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity((long) quantity)
                                .setPrice(product.getDefaultPrice())
                                .build()
                );
                entity.getFields().forEach(field -> {
                    SessionCreateParams.CustomField.Builder builder = SessionCreateParams.CustomField.builder()
                            .setType(field.getType())
                            .setLabel(SessionCreateParams.CustomField.Label.builder()
                                    .setType(SessionCreateParams.CustomField.Label.Type.CUSTOM)
                                    .setCustom(product.getName() + " - " + field.getLabel())
                                    .build())
                            .setKey(product.getId() + "-" + field.getId())
                            .setOptional(field.isOptional());

                    if(field.getType() == SessionCreateParams.CustomField.Type.DROPDOWN) {
                        List<SessionCreateParams.CustomField.Dropdown.Option> options = new ArrayList<>();

                        field.getOptions().forEach(option -> {
                            options.add(SessionCreateParams.CustomField.Dropdown.Option.builder()
                                            .setLabel(option.getLabel())
                                            .setValue(option.getValue())
                                    .build()
                            );
                        });

                        builder = builder.setDropdown(SessionCreateParams.CustomField.Dropdown.builder()
                                .addAllOption(options)
                                .build()
                        );
                    }

                    customFields.add(builder.build());
                });
            } catch (StripeException e) {
                e.printStackTrace();
            }
        });

        if(items.isEmpty()) {
            return ResponseEntity.status(303).location(new URI(FAIL_URL)).build();
        }

        UserEntity user = null;
        if(authentication != null) {
            user = ((User)authentication.getPrincipal()).getEntity();
        }

        orderProductRepository.saveAll(opEntities);
        OrderEntity orderEntity = OrderEntity.builder()
                .user(user)
                .products(opEntities)
                .status(OrderEntity.OrderStatus.PAYING)
                .paymentUrl("")
                .paymentDate(new Timestamp(Instant.now().toEpochMilli()))
                .build();
        orderRepository.save(orderEntity);

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(SUCCESS_URL)
                .setCancelUrl(FAIL_URL)
                .setAllowPromotionCodes(true)
                .setCustomerEmail(user != null ? user.getEmail() : null)
                .addAllCustomField(customFields)
                .addCustomField(SessionCreateParams.CustomField.builder().setType(SessionCreateParams.CustomField.Type.NUMERIC).build())
                .setPhoneNumberCollection(SessionCreateParams.PhoneNumberCollection.builder().setEnabled(
                        properties.isStripeCollectPhoneNumber()
                ).build())
                .setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.valueOf(
                        properties.isStripeCollectBillingAddress() ? "REQUIRED" : "AUTO"
                ))
                .setInvoiceCreation(
                        SessionCreateParams.InvoiceCreation.builder()
                                .setEnabled(true)
                                .build()
                )
                .setAutomaticTax(
                        SessionCreateParams.AutomaticTax.builder().setEnabled(true).build()
                )
                .addAllLineItem(items)
                .putMetadata("order_id", String.valueOf(orderEntity.getId()))
                .build();

        Session session = Session.create(params);

        orderEntity.setPaymentUrl(session.getUrl());
        orderEntity.setStripeId(session.getId());
        orderRepository.save(orderEntity);

        return ResponseEntity.status(303).location(new URI(session.getUrl())).build();
    }

    @SneakyThrows
    @PostMapping("/handle")
    public JsonResponse<Void> handlePayment(@RequestBody String payload, @RequestHeader("Stripe-Signature") String signature) {
        Event event = null;

        try {
            event = Webhook.constructEvent(payload, signature,properties.getStripeWebhookKey());
        } catch (Exception e) {
            return new JsonResponse<>(HttpStatus.BAD_REQUEST, "Invalid payload or signature");
        }

        Optional<StripeObject> optionalObj= event.getDataObjectDeserializer().getObject();

        if(optionalObj.isEmpty()) {
            return new JsonResponse<>(HttpStatus.BAD_REQUEST, "Object empty");
        }

        StripeObject stripeObject = optionalObj.get();
        Map<String,String> metadata = new HashMap<>();

        if(stripeObject instanceof MetadataStore<?>) {
            metadata = ((MetadataStore<?>) stripeObject).getMetadata();
        }
        if(stripeObject instanceof Session) {
            metadata = ((Session) stripeObject).getMetadata();
        }

        if(!metadata.containsKey("order_id")) {
            return new JsonResponse<>(HttpStatus.BAD_REQUEST, "");
        }

        int orderId = Integer.parseInt(metadata.get("order_id"));
        Optional<OrderEntity> orderEntityOpt = orderRepository.findById(orderId);

        if(orderEntityOpt.isEmpty()) {
            return new JsonResponse<>(HttpStatus.BAD_REQUEST, "");
        }

        OrderEntity orderEntity = orderEntityOpt.get();

        switch (event.getType()) {
            case "checkout.session.completed":
            case "checkout.session.async_payment_succeeded":
                Session session = (Session) stripeObject;
                if(!session.getPaymentStatus().equalsIgnoreCase("unpaid")) {
                    orderEntity.setStatus(OrderEntity.OrderStatus.PAID);
                    orderEntity.setPaymentDate(new Timestamp(Instant.now().toEpochMilli()));
                    orderEntity.setStripeId(session.getPaymentIntent());
                    orderEntity.setPaymentUrl(null);

                    Session.CustomerDetails details = session.getCustomerDetails();

                    String email =details.getEmail();
                    String customer = details.getName();
                    if(customer == null) customer = email;

                    orderEntity.setOrderEmail(email);
                    orderEntity.setCustomer(customer);

                    if(properties.isStripeCollectPhoneNumber()) {
                        orderEntity.setPhoneNumber(details.getPhone());
                    }

                    if(properties.isStripeCollectBillingAddress()) {
                        orderEntity.setCountry(details.getAddress().getCountry());
                        orderEntity.setCity(details.getAddress().getCity());
                        orderEntity.setPostalCode(details.getAddress().getPostalCode());
                        orderEntity.setState(details.getAddress().getState());
                        orderEntity.setAddress1(details.getAddress().getLine1());
                        orderEntity.setAddress2(details.getAddress().getLine2());
                    }

                    Invoice invoice = Invoice.retrieve(session.getInvoice());

                    InputStream in = new URL(invoice.getInvoicePdf()).openStream();
                    Files.copy(in, Paths.get("./assets/invoices/"+invoice.getNumber()+".pdf"), StandardCopyOption.REPLACE_EXISTING);
                    in.close();

                    Charge charge = Charge.retrieve(invoice.getCharge());
                    String receiptUrl = charge.getReceiptUrl().split("\\?", 2)[0] + "/pdf?s=ap";

                    in = new URL(receiptUrl).openStream();
                    Files.copy(in, Paths.get("./assets/receipts/"+invoice.getNumber()+".pdf"), StandardCopyOption.REPLACE_EXISTING);
                    in.close();

                    orderEntity.setInvoiceNumber(invoice.getNumber());

                    Map<Integer, List<OrderProductField>> fieldsData = new HashMap<>();

                    session.getCustomFields().forEach(field -> {
                        String[] key = field.getKey().split("-",2);
                        int pId = Integer.parseInt(key[0]);
                        int fId = Integer.parseInt(key[1]);

                        Optional<ProductEntity> productEntityOpt = productRepository.findById(pId);
                        Optional<ProductField> fieldEntityOpt = productFieldRepository.findById(fId);

                        if(productEntityOpt.isEmpty() || fieldEntityOpt.isEmpty()) return;

                        OrderProductField opf = new OrderProductField(
                                fieldEntityOpt.get(),field.getText() != null ? field.getText().getValue() : null
                        );
                        orderProductFieldRepository.save(opf);
                        fieldsData.putIfAbsent(pId, new ArrayList<>());
                        fieldsData.get(pId).add(opf);
                    });

                    orderEntity.getProducts().forEach(opEntity -> {
                        if(fieldsData.containsKey(opEntity.getProduct().getId())) {
                            opEntity.setFields(fieldsData.get(opEntity.getProduct().getId()));
                        }
                        orderProductRepository.save(opEntity);
                    });

                    orderRepository.save(orderEntity);

                    // TODO: Execute order

                    orderEntity.setStatus(OrderEntity.OrderStatus.COMPLETED);
                    orderEntity.setCompletionDate(new Timestamp(Instant.now().toEpochMilli()));
                    orderRepository.save(orderEntity);
                }
                break;
            case "checkout.session.async_payment_failed":
            case "checkout.session.expired":
                orderEntity.setStatus(OrderEntity.OrderStatus.FAILED);
                orderRepository.save(orderEntity);
                break;
        }

        return new JsonResponse<>(HttpStatus.OK, "");
    }

    @GetMapping("/document")
    private ResponseEntity<Resource> document(@RequestParam(name = "type") String type, @RequestParam(name = "number") String number, Authentication authentication) throws IOException {
        if(!type.equalsIgnoreCase("invoice") && !type.equalsIgnoreCase("receipt")) {
            return ResponseEntity.status(400).build();
        }

        Optional<OrderEntity> orderOpt = orderRepository.findByInvoiceNumber(number);

        if(orderOpt.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        OrderEntity entity = orderOpt.get();
        if(entity.getUser() != null) {
            if(authentication == null) return ResponseEntity.status(403).build();
            UserEntity user = ((User)authentication.getPrincipal()).getEntity();
            if(user.getId() != entity.getUser().getId()) {
                return ResponseEntity.status(403).build();
            }
        }

        java.io.File file = new File("./assets/"+type+"s/"+number+".pdf");
        Path path = file.toPath();
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+type+"_"+number+".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
