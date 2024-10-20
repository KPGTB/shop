package eu.kpgtb.shop.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Entity(name = "shop_order")
public class OrderEntity extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany
    @JoinColumn(name = "order_id")
    private List<OrderProductEntity> products;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String stripeId;

    private Timestamp orderDate;
    private Timestamp paymentDate;
    private Timestamp completionDate;

    public enum OrderStatus {
        PAYING, PAID, COMPLETED, FAILED
    }
}
