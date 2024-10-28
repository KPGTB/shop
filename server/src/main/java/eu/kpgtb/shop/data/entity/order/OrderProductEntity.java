package eu.kpgtb.shop.data.entity.order;

import eu.kpgtb.shop.data.entity.BaseEntity;
import eu.kpgtb.shop.data.entity.product.ProductEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity(name = "order_product")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class OrderProductEntity extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "product_id")
    ProductEntity product;
    int quantity;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_product_id")
    List<OrderFieldEntity> fields;
}
