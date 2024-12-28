package eu.kpgtb.shop.controller;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
import eu.kpgtb.shop.data.entity.order.OrderFieldEntity;
import eu.kpgtb.shop.data.entity.product.*;
import eu.kpgtb.shop.data.entity.UserEntity;
import eu.kpgtb.shop.data.repository.order.OrderProductFieldRepository;
import eu.kpgtb.shop.data.repository.order.OrderProductRepository;
import eu.kpgtb.shop.data.repository.order.OrderRepository;
import eu.kpgtb.shop.data.repository.product.*;
import eu.kpgtb.shop.serivce.iface.IRconService;
import eu.kpgtb.shop.serivce.iface.IS3Service;
import eu.kpgtb.shop.util.AesUtil;
import eu.kpgtb.shop.util.JsonResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    @Autowired private ProductRepository productRepository;
    @Autowired private OrderProductRepository orderProductRepository;
    @Autowired private OrderProductFieldRepository orderProductFieldRepository;
    @Autowired private ProductFieldRepository productFieldRepository;
    @Autowired private OrderRepository orderRepository;

    @Autowired private RconRepository rconRepository;
    @Autowired private ProductActionKeyRepository keyRepository;
    @Autowired private ApiRepository apiRepository;
    @Autowired private ProductSecretRepository productSecretRepository;

    @Autowired private RestTemplate restTemplate;
    @Autowired private IRconService rconService;

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
    public ResponseEntity<Void> pay(@RequestBody MultiValueMap<String,String> body, @AuthenticationPrincipal User principal) throws URISyntaxException, StripeException {
        List<SessionCreateParams.LineItem> items = new ArrayList<>();
        List<SessionCreateParams.CustomField> customFields = new ArrayList<>();
        List<OrderProductEntity> opEntities = new ArrayList<>();

        AtomicReference<Double> subtotal = new AtomicReference<>(0.0);
        AtomicReference<Double> estimatedTax = new AtomicReference<>(0.0);
        AtomicReference<Double> estimatedTotal = new AtomicReference<>(0.0);

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

                double pSub = entity.getPrice() * quantity;
                double pTax = pSub * (entity.getDisplayTax() / 100.0);
                double pTotal = pSub + pTax;

                subtotal.updateAndGet(v -> v + pSub);
                estimatedTax.updateAndGet(v -> v + pTax);
                estimatedTotal.updateAndGet(v -> v + pTotal);

            } catch (StripeException e) {
                e.printStackTrace();
                return;
            }

            for (int i = 0; i <= quantity; i++) {
                customFields.addAll(entity.getFields()
                        .stream()
                        .map(field -> field.getStripeObject(pId, product.getName(),quantity)).toList());
            }
        });
        if(items.isEmpty()) return ResponseEntity.status(303).location(new URI(FAIL_URL)).build();

        // Save basic data to database
        UserEntity user = principal != null ? principal.getEntity() : null;

        OrderEntity orderEntity = OrderEntity.builder()
                .user(user)
                .products(opEntities)
                .status(OrderEntity.OrderStatus.PAYING)
                .orderDate(new Timestamp(Instant.now().toEpochMilli()))
                .total(estimatedTotal.get())
                .subtotal(subtotal.get())
                .tax(estimatedTax.get())
                .discount(0.0)
                .currency(properties.getStripeCurrency())
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
                if(orderEntity.getStatus() == OrderEntity.OrderStatus.PAYING) {
                    orderEntity.setStatus(OrderEntity.OrderStatus.FAILED);
                    orderRepository.save(orderEntity);
                }

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
        Session.TotalDetails totalDetails = session.getTotalDetails();

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
        orderEntity.setTotal(session.getAmountTotal() / 100.0);
        orderEntity.setSubtotal(session.getAmountSubtotal() / 100.0);
        orderEntity.setTax(totalDetails.getAmountTax() != null ? totalDetails.getAmountTax() / 100.0 : 0.0);
        orderEntity.setDiscount(totalDetails.getAmountDiscount() != null ? totalDetails.getAmountDiscount() / 100.0 : 0.0);
        orderEntity.setCurrency(orderEntity.getCurrency());

        // Read custom fields
        Map<Integer, List<OrderFieldEntity>> fieldsData = new HashMap<>();
        session.getCustomFields().forEach(field -> {
            String[] key = field.getKey().split("-",3);
            int pId = Integer.parseInt(key[0]);
            int fId = Integer.parseInt(key[1]);
            int place = Integer.parseInt(key[2]);

            Optional<ProductEntity> productEntityOpt = productRepository.findById(pId);
            Optional<ProductFieldEntity> fieldEntityOpt = productFieldRepository.findById(fId);

            if(productEntityOpt.isEmpty() || fieldEntityOpt.isEmpty()) return;

            String value;
            switch (field.getType()) {
                case "text" -> value = field.getText().getValue();
                case "numeric" -> value = field.getNumeric().getValue();
                case "dropdown" -> value = field.getDropdown().getValue();
                default -> value = null;
            }

            OrderFieldEntity opf = new OrderFieldEntity(fieldEntityOpt.get(),value,place);
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
        orderEntity.getProducts().forEach(product -> {
            List<List<OrderFieldEntity>> fields = new ArrayList<>();
            for (int i = 0; i < product.getQuantity(); i++) {
                fields.add(new ArrayList<>());
            }
            product.getFields().forEach(field -> {
                fields.get(field.getPlace()).add(field);
            });

            List<String> filesToDeliver = new ArrayList<>();
            List<String> keysToDeliver = new ArrayList<>();

            for (int i = 0; i < product.getQuantity(); i++) {
                product.getProduct().getActions().forEach(action -> {
                    switch (action.getType()) {
                        case API -> {
                            JsonObject json = new Gson().fromJson(action.getData(),JsonObject.class);

                            int apiId = json.get("api_id").getAsInt();
                            String method = json.get("method").getAsString();
                            JsonElement content = json.get("content");

                            ApiEntity api = apiRepository.findById(apiId).orElse(null);
                            if(api != null) {

                                String url = api.getUrl();
                                if(method.equalsIgnoreCase("get")) {
                                    url += "?" + content.getAsString();
                                }

                                HttpEntity<?> entity = new HttpEntity<>(content);
                                entity.getHeaders().add("Content-Type", "application/json");
                                if(api.getToken() != null && !api.getToken().isEmpty()) {
                                    entity.getHeaders().setBearerAuth(api.getToken());
                                }

                                restTemplate.exchange(url, HttpMethod.valueOf(method.toUpperCase()), entity,String.class);
                            }
                        }
                        case KEY -> {
                            ProductActionKeyEntity key = keyRepository.findByType(action.getData());
                            if(key != null) {
                                keysToDeliver.add(key.getKey());
                                keyRepository.delete(key);
                            }
                        }
                        case FILE -> {
                            filesToDeliver.add(action.getData());
                        }
                        case RCON -> {
                            JsonObject json = new Gson().fromJson(action.getData(),JsonObject.class);

                            int rconId = json.get("rcon_id").getAsInt();
                            String command = json.get("command").getAsString();

                            RconEntity rconEntity = rconRepository.findById(rconId).orElse(null);
                            if(rconEntity != null) {
                                try {
                                    rconService.sendMessage(rconEntity.getHost(), AesUtil.decrypt(rconEntity.getPassword(), properties.getAesSecretKey()), command);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
            }
        });

        // TODO:
        // - Sending email
        // - Secret variables
        // - Custom fields variables

        orderEntity.setStatus(OrderEntity.OrderStatus.COMPLETED);
        orderEntity.setCompletionDate(new Timestamp(Instant.now().toEpochMilli()));
        orderRepository.save(orderEntity);
    }

    @GetMapping("/document/{type}/{invoiceId}")
    public ResponseEntity<Resource> document(@PathVariable String type, @PathVariable String invoiceId, @AuthenticationPrincipal User principal) throws IOException {
        if(!Arrays.asList("invoice", "receipt").contains(type)) return ResponseEntity.status(400).build();

        // Check if invoice exists
        OrderEntity entity = orderRepository.findByInvoiceNumber(invoiceId).orElse(null);
        if(entity == null) return ResponseEntity.status(404).build();

        // Check permissions
        if(entity.getUser() != null) {
            if(principal == null) return ResponseEntity.status(403).build();
            UserEntity user = principal.getEntity();
            if(user.getId() != entity.getUser().getId() && user.getRole() != UserEntity.UserRole.BUSINESS) return ResponseEntity.status(403).build();
        }

        // Download invoice/receipt
        byte[] bytes = s3Service.getFile(type+"_"+invoiceId+".pdf", properties.getS3PrivateBucket());
        ByteArrayResource resource = new ByteArrayResource(bytes);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+type+"_"+invoiceId+".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}
