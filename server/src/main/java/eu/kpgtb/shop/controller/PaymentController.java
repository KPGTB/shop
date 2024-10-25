package eu.kpgtb.shop.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionRetrieveParams;
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
import eu.kpgtb.shop.serivce.iface.IS3Service;
import eu.kpgtb.shop.util.JsonResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    @Autowired private ProductRepository productRepository;
    @Autowired private OrderProductRepository orderProductRepository;
    @Autowired private OrderProductFieldRepository orderProductFieldRepository;
    @Autowired private ProductFieldRepository productFieldRepository;
    @Autowired private OrderRepository orderRepository;

    @Autowired private IS3Service s3Service;
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
        List<SessionCreateParams.CustomField> customFields = new ArrayList<>();
        List<OrderProductEntity> opEntities = new ArrayList<>();

        // Collect products from body
        body.toSingleValueMap().forEach((pIdStr, quantityStr) -> {
            int pId = Integer.parseInt(pIdStr);
            int quantity = Integer.parseInt(quantityStr);
            ProductEntity entity = productRepository.findById(pId).orElse(null);

            if(entity == null || quantity < 1) return;
            opEntities.add(new OrderProductEntity(entity, quantity, new ArrayList<>()));

            Product product;
            try {
                product = Product.retrieve(entity.getStripeId());
                items.add(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity((long) quantity)
                                .setPrice(product.getDefaultPrice())
                                .build()
                );
            } catch (StripeException e) {
                e.printStackTrace();
                return;
            }

            customFields.addAll(entity.getFields()
                    .stream()
                    .map(field -> field.getStripeObject(pId, product.getName())).toList());
        });
        if(items.isEmpty()) return ResponseEntity.status(303).location(new URI(FAIL_URL)).build();

        // Save basic data to database
        UserEntity user = authentication != null ?
                ((User)authentication.getPrincipal()).getEntity() : null;

        OrderEntity orderEntity = OrderEntity.builder()
                .user(user)
                .products(opEntities)
                .status(OrderEntity.OrderStatus.PAYING)
                .orderDate(new Timestamp(Instant.now().toEpochMilli()))
                .build();
        orderRepository.save(orderEntity);

        // Prepare checkout
        SessionCreateParams.Builder params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(SUCCESS_URL)
                .setCancelUrl(FAIL_URL)
                .setAllowPromotionCodes(true)
                .setCustomerEmail(user != null ? user.getEmail() : null)
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
                .putMetadata("order_id", String.valueOf(orderEntity.getId()));
        if(!customFields.isEmpty()) params = params.addAllCustomField(customFields);
        Session session = Session.create(params.build());

        // Update basic data in database
        orderEntity.setPaymentUrl(session.getUrl());
        orderEntity.setStripeId(session.getId());
        orderRepository.save(orderEntity);

        // Redirect to checkout
        return ResponseEntity.status(303).location(new URI(session.getUrl())).build();
    }

    @SneakyThrows
    @PostMapping("/handle")
    public JsonResponse<Void> handlePayment(@RequestBody String payload, @RequestHeader("Stripe-Signature") String signature) {
        // Validate webhook
        Event event = null;
        try {
            event = Webhook.constructEvent(payload, signature,properties.getStripeWebhookKey());
        } catch (Exception e) {
            return new JsonResponse<>(HttpStatus.BAD_REQUEST, "Invalid payload or signature");
        }
        StripeObject stripeObject= event.getDataObjectDeserializer().getObject().orElse(null);
        if(stripeObject == null) return new JsonResponse<>(HttpStatus.BAD_REQUEST, "Object empty");

        // Validate metadata
        Map<String,String> metadata = new HashMap<>();
        if(stripeObject instanceof MetadataStore<?>) metadata = ((MetadataStore<?>) stripeObject).getMetadata();
        if(stripeObject instanceof Session) metadata = ((Session) stripeObject).getMetadata();
        if(!metadata.containsKey("order_id")) return new JsonResponse<>(HttpStatus.BAD_REQUEST, "Invalid metadata");

        // Validate order
        int orderId = Integer.parseInt(metadata.get("order_id"));
        OrderEntity orderEntity = orderRepository.findById(orderId).orElse(null);
        if(orderEntity == null) return new JsonResponse<>(HttpStatus.BAD_REQUEST, "Order not found");

        // Fulfill order
        switch (event.getType()) {
            case "checkout.session.completed":
            case "checkout.session.async_payment_succeeded":
                Session session = Session.retrieve(
                        ((Session)stripeObject).getId(),
                        SessionRetrieveParams.builder()
                                .addExpand("invoice")
                                .addExpand("invoice.charge")
                                .build(),
                        null
                );

                fulfillOrder(session, orderEntity);
                return new JsonResponse<>(HttpStatus.OK, "");
            case "checkout.session.async_payment_failed":
            case "checkout.session.expired":
                orderEntity.setStatus(OrderEntity.OrderStatus.FAILED);
                orderRepository.save(orderEntity);
                break;
        }

        return new JsonResponse<>(HttpStatus.OK, "");
    }

    @Async
    private void fulfillOrder(Session session, OrderEntity orderEntity) {
        if(session.getPaymentStatus().equalsIgnoreCase("unpaid")) return;
        Session.CustomerDetails details = session.getCustomerDetails();
        Invoice invoice = session.getInvoiceObject();
        Charge charge = session.getInvoiceObject().getChargeObject();

        String receiptUrl = charge.getReceiptUrl().split("\\?", 2)[0] + "/pdf?s=ap";
        String invoiceUrl = invoice.getInvoicePdf();
        String invoiceNumber = invoice.getNumber();

        // Add more data to database
        orderEntity.setStatus(OrderEntity.OrderStatus.PAID);
        orderEntity.setPaymentDate(new Timestamp(Instant.now().toEpochMilli()));
        orderEntity.setStripeId(session.getPaymentIntent());
        orderEntity.setPaymentUrl(null);
        orderEntity.setContactData(details);
        orderEntity.setBillingAddress(details);
        orderEntity.setInvoiceNumber(invoiceNumber);

        // Read custom fields
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

            fieldsData.putIfAbsent(pId, new ArrayList<>());
            fieldsData.get(pId).add(opf);
        });

        // Save input from custom fields
        orderEntity.getProducts().forEach(opEntity -> {
            int id = opEntity.getProduct().getId();
            if(fieldsData.containsKey(id)) opEntity.setFields(fieldsData.get(opEntity.getProduct().getId()));
        });

        // Finish order payment
        orderRepository.save(orderEntity);
        uploadInvoice(invoiceUrl,receiptUrl,invoiceNumber);
        completeOrder(orderEntity);
    }

    @Async
    private void uploadInvoice(String invoiceUrl, String receiptUrl, String invoiceNumber) {
        s3Service.uploadFile(invoiceUrl, "invoice_" + invoiceNumber + ".pdf", properties.getS3PrivateBucket());
        s3Service.uploadFile(receiptUrl, "receipt_" + invoiceNumber + ".pdf", properties.getS3PrivateBucket());
    }

    @Async
    private void completeOrder(OrderEntity orderEntity) {
        // TODO: Execute actions

        orderEntity.setStatus(OrderEntity.OrderStatus.COMPLETED);
        orderEntity.setCompletionDate(new Timestamp(Instant.now().toEpochMilli()));
        orderRepository.save(orderEntity);
    }

    @GetMapping("/document")
    private ResponseEntity<Resource> document(@RequestParam(name = "type") String type, @RequestParam(name = "number") String number, Authentication authentication) throws IOException {
        if(!Arrays.asList("invoice", "receipt").contains(type)) return ResponseEntity.status(400).build();

        // Check if invoice exists
        OrderEntity entity = orderRepository.findByInvoiceNumber(number).orElse(null);
        if(entity == null) return ResponseEntity.status(404).build();

        // Check permissions
        if(entity.getUser() != null) {
            if(authentication == null) return ResponseEntity.status(403).build();
            UserEntity user = ((User)authentication.getPrincipal()).getEntity();
            if(user.getId() != entity.getUser().getId()) return ResponseEntity.status(403).build();
        }

        // Download invoice/receipt
        byte[] bytes = s3Service.getFile(type+"_"+number+".pdf", properties.getS3PrivateBucket());
        ByteArrayResource resource = new ByteArrayResource(bytes);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+type+"_"+number+".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}
