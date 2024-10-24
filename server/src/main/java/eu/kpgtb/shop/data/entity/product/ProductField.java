package eu.kpgtb.shop.data.entity.product;

import com.stripe.param.checkout.SessionCreateParams;
import eu.kpgtb.shop.data.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Entity
public class ProductField extends BaseEntity {
    private String label;
    private boolean optional;
    @Enumerated(EnumType.STRING) private SessionCreateParams.CustomField.Type type;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "field_id")
    private List<ProductDropdownOption> options;

    public SessionCreateParams.CustomField getStripeObject(int pId, String productName) {
        return SessionCreateParams.CustomField.builder()
                .setType(this.type)
                .setLabel(SessionCreateParams.CustomField.Label.builder()
                        .setType(SessionCreateParams.CustomField.Label.Type.CUSTOM)
                        .setCustom(productName + " - " + this.label)
                        .build())
                .setKey(pId + "-" + this.getId())
                .setOptional(this.optional)
                .setDropdown(
                        this.type != SessionCreateParams.CustomField.Type.DROPDOWN ?
                            null
                        :
                            SessionCreateParams.CustomField.Dropdown.builder()
                                .addAllOption(
                                        this.options.stream().map(ProductDropdownOption::getStripeObject).toList()
                                ).build()
                ).build();
    }
}
