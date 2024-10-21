package eu.kpgtb.shop.data.entity.order;

import eu.kpgtb.shop.data.entity.BaseEntity;
import eu.kpgtb.shop.data.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
@Entity(name = "shop_order")
public class OrderEntity extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private String orderEmail;
    private String customer;
    private String phoneNumber;
    private String country;
    private String state;
    private String address1;
    private String address2;
    private String postalCode;
    private String city;

    @OneToMany
    @JoinColumn(name = "order_id")
    private List<OrderProductEntity> products;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String stripeId;
    @Lob private String paymentUrl;
    @Lob private String invoiceNumber;

    private Timestamp orderDate;
    private Timestamp paymentDate;
    private Timestamp completionDate;

    public enum OrderStatus {
        PAYING, PAID, COMPLETED, FAILED
    }
}
