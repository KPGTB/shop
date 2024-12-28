package eu.kpgtb.shop.data.entity.product;

import eu.kpgtb.shop.data.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "api_data")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ApiEntity extends BaseEntity {
    String displayName;
    String url;
    String token;
}
