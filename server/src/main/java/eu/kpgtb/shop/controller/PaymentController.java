package eu.kpgtb.shop.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Product;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import eu.kpgtb.shop.auth.User;
import eu.kpgtb.shop.config.Properties;
import eu.kpgtb.shop.data.entity.OrderEntity;
import eu.kpgtb.shop.data.entity.OrderProductEntity;
import eu.kpgtb.shop.data.entity.ProductEntity;
import eu.kpgtb.shop.data.entity.UserEntity;
import eu.kpgtb.shop.data.repository.OrderProductRepository;
import eu.kpgtb.shop.data.repository.OrderRepository;
import eu.kpgtb.shop.data.repository.ProductRepository;
import eu.kpgtb.shop.util.JsonResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderProductRepository orderProductRepository;
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
    public ResponseEntity<Void> pay(PaymentBody body, Authentication authentication) throws URISyntaxException, StripeException {
        List<SessionCreateParams.LineItem> items = new ArrayList<>();
        List<OrderProductEntity> opEntities = new ArrayList<>();

        Arrays.stream(body.products
                .replace(" ", "")
                .split(",")).forEach(dataLine -> {
                    String[] data = dataLine.split(":",2);
                    int pId = Integer.parseInt(data[0]);
                    int quantity = Integer.parseInt(data[1]);

                    if(quantity < 1) return;
                    Optional<ProductEntity> entityOpt = productRepository.findById(pId);
                    if(entityOpt.isEmpty()) return;
                    try {
                        Product product = Product.retrieve(entityOpt.get().getStripeId());
                        opEntities.add(new OrderProductEntity(entityOpt.get(), quantity));
                        items.add(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity((long) quantity)
                                        .setPrice(product.getDefaultPrice())
                                        .build()
                        );
                    } catch (StripeException e) {
                        e.printStackTrace();
            }
        });

        if(items.isEmpty()) {
            return ResponseEntity.status(303).location(new URI(FAIL_URL)).build();
        }

        UserEntity user = ((User)authentication.getPrincipal()).getEntity();

        orderProductRepository.saveAll(opEntities);
        OrderEntity orderEntity = new OrderEntity(
                user,
                opEntities,
                OrderEntity.OrderStatus.PAYING,
                "",
                new Timestamp(Instant.now().toEpochMilli()),
                null,
                null
        );
        orderRepository.save(orderEntity);

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(SUCCESS_URL)
                .setCancelUrl(FAIL_URL)
                .setAllowPromotionCodes(true)
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
        return ResponseEntity.status(303).location(new URI(session.getUrl())).build();
    }

    @PostMapping("/handle")
    public JsonResponse<Void> handlePayment(HttpServletRequest request) {
        String signature = request.getHeader("Stripe-Signature");
        Event event = null;

        try {
            String payload = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            System.out.println(payload);
            event = Webhook.constructEvent(payload, signature,properties.getStripeWebhookKey());
        } catch (Exception e) {
            return new JsonResponse<>(HttpStatus.BAD_REQUEST, "");
        }

        Session session= (Session) event.getDataObjectDeserializer().getObject().get();

        if(!session.getMetadata().containsKey("order_id")) {
            return new JsonResponse<>(HttpStatus.BAD_REQUEST, "");
        }

        int orderId = Integer.parseInt(session.getMetadata().get("order_id"));
        Optional<OrderEntity> orderEntityOpt = orderRepository.findById(orderId);

        if(orderEntityOpt.isEmpty()) {
            return new JsonResponse<>(HttpStatus.BAD_REQUEST, "");
        }

        OrderEntity orderEntity = orderEntityOpt.get();

        switch (event.getType()) {
            case "checkout.session.completed":
            case "checkout.session.async_payment_succeeded":
                if(!session.getPaymentStatus().equalsIgnoreCase("unpaid")) {
                    orderEntity.setStatus(OrderEntity.OrderStatus.PAID);
                    orderEntity.setPaymentDate(new Timestamp(Instant.now().toEpochMilli()));
                    orderEntity.setStripeId(session.getPaymentIntent());
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

    // ID:quantity,ID:quantity
    record PaymentBody(String products) {}
}
