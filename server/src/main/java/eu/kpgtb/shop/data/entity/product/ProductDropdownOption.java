package eu.kpgtb.shop.data.entity.product;

import com.stripe.param.checkout.SessionCreateParams;
import eu.kpgtb.shop.data.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Entity
public class ProductDropdownOption extends BaseEntity {
    private String label;
    private String value;

    public SessionCreateParams.CustomField.Dropdown.Option getStripeObject() {
        return SessionCreateParams.CustomField.Dropdown.Option.builder()
                .setLabel(this.label)
                .setValue(this.value)
                .build();
    }
}
