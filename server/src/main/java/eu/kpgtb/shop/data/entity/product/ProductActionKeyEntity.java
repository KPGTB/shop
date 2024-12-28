package eu.kpgtb.shop.data.entity.product;

import eu.kpgtb.shop.data.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "product_action_key")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ProductActionKeyEntity extends BaseEntity {
    String type;
    String key;
}
