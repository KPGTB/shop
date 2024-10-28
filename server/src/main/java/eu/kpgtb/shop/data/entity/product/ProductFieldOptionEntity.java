package eu.kpgtb.shop.data.entity.product;

import com.stripe.param.checkout.SessionCreateParams;
import eu.kpgtb.shop.data.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "product_field_option")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ProductFieldOptionEntity extends BaseEntity {
    String label;
    String value;

    public SessionCreateParams.CustomField.Dropdown.Option getStripeObject() {
        return SessionCreateParams.CustomField.Dropdown.Option.builder()
                .setLabel(this.label)
                .setValue(this.value)
                .build();
    }
}
