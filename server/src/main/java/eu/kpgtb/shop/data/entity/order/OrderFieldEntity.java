package eu.kpgtb.shop.data.entity.order;

import eu.kpgtb.shop.data.entity.BaseEntity;
import eu.kpgtb.shop.data.entity.product.ProductFieldEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "order_field")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class OrderFieldEntity extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "field_id")
    ProductFieldEntity field;
    String value;
}
