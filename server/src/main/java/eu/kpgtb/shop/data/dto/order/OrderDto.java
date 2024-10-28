package eu.kpgtb.shop.data.dto.order;

import eu.kpgtb.shop.data.dto.UserDto;
import eu.kpgtb.shop.data.entity.order.OrderEntity;
import eu.kpgtb.shop.data.entity.order.OrderProductEntity;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
public class OrderDto  {
    private final int id;
    private final int userId;
    private final UserDto user;

    private final String orderEmail;
    private final String customer;
    private final String phoneNumber;
    private final String country;
    private final String state;
    private final String address1;
    private final String address2;
    private final String postalCode;
    private final String city;

    private final List<Integer> productsId;
    private final List<OrderProductDto> products;

    private final String status;

    private final String stripeId;
    private final String paymentUrl;
    private final String invoiceNumber;

    private final double total;
    private final double subtotal;
    private final double tax;
    private final double discount;

    private final Timestamp orderDate;
    private final Timestamp paymentDate;
    private final Timestamp completionDate;

    public OrderDto(OrderEntity entity) {
        this(entity, new ArrayList<>(), "");
    }
    public OrderDto(OrderEntity entity, List<String> expands, String path) {
        this.id = entity.getId();
        this.orderEmail = entity.getOrderEmail();
        this.customer = entity.getCustomer();
        this.phoneNumber = entity.getPhoneNumber();
        this.country = entity.getCountry();
        this.state = entity.getState();
        this.address1 = entity.getAddress1();
        this.address2 = entity.getAddress2();
        this.postalCode = entity.getPostalCode();
        this.city = entity.getCity();
        this.status = entity.getStatus().name();
        this.stripeId = entity.getStripeId();
        this.paymentUrl = entity.getPaymentUrl();
        this.invoiceNumber = entity.getInvoiceNumber();
        this.total = entity.getTotal();
        this.subtotal = entity.getSubtotal();
        this.tax = entity.getTax();
        this.discount = entity.getDiscount();
        this.orderDate = entity.getOrderDate();
        this.paymentDate = entity.getPaymentDate();
        this.completionDate = entity.getCompletionDate();

        this.userId = entity.getUser().getId();
        this.user = expands.contains(path + "user") ? new UserDto(entity.getUser(),expands, path + "user.") : null;

        this.productsId = entity.getProducts().stream().map(OrderProductEntity::getId).toList();
        this.products = expands.contains(path + "products") ?
                entity.getProducts().stream().map(product -> new OrderProductDto(product,expands, path + "products.")).toList() : new ArrayList<>();
    }
}
