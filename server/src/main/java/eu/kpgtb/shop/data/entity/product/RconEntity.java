package eu.kpgtb.shop.data.entity.product;

import eu.kpgtb.shop.data.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "rcon_data")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class RconEntity extends BaseEntity {
    String displayName;
    String host;
    String password;
}
