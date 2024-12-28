package eu.kpgtb.shop.data.entity.product;

import eu.kpgtb.shop.data.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "product_secret")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ProductSecretEntity extends BaseEntity {
    String code;
    String secret;
}
