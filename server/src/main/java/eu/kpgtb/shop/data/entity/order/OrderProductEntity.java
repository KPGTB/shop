package eu.kpgtb.shop.data.entity.order;

import eu.kpgtb.shop.data.entity.BaseEntity;
import eu.kpgtb.shop.data.entity.product.ProductEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Entity(name = "order_product")
public class OrderProductEntity extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;
    private int quantity;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_product_id")
    List<OrderProductField> fields;
}
