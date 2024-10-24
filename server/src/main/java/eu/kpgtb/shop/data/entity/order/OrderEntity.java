package eu.kpgtb.shop.data.entity.order;

import com.stripe.model.checkout.Session;
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

    @OneToMany(cascade = CascadeType.ALL)
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

    public void setContactData(Session.CustomerDetails details) {
        this.orderEmail =details.getEmail();
        this.phoneNumber = details.getPhone();
        this.customer = details.getName();
        if(this.customer == null) this.customer = this.orderEmail;
    }

    public void setBillingAddress(Session.CustomerDetails details) {
        this.country = details.getAddress().getCountry();
        this.city = details.getAddress().getCity();
        this.postalCode = details.getAddress().getPostalCode();
        this.state = details.getAddress().getState();
        this.address1 = details.getAddress().getLine1();
        this.address2 = details.getAddress().getLine2();
    }

    public enum OrderStatus {
        PAYING, PAID, COMPLETED, FAILED
    }
}
