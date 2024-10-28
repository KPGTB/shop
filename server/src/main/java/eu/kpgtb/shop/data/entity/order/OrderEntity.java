package eu.kpgtb.shop.data.entity.order;

import com.stripe.model.checkout.Session;
import eu.kpgtb.shop.data.entity.BaseEntity;
import eu.kpgtb.shop.data.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Entity(name = "shop_order")
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class OrderEntity extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id")
    UserEntity user;

    String orderEmail;
    String customer;
    String phoneNumber;
    String country;
    String state;
    String address1;
    String address2;
    String postalCode;
    String city;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    List<OrderProductEntity> products;

    @Enumerated(EnumType.STRING)
    OrderStatus status;

    String stripeId;
    @Lob
    String paymentUrl;
    @Lob
    String invoiceNumber;

    double total;
    double subtotal;
    double tax;
    double discount;

    Timestamp orderDate;
    Timestamp paymentDate;
    Timestamp completionDate;

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
