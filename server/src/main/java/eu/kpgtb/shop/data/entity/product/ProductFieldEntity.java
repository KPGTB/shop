package eu.kpgtb.shop.data.entity.product;

import com.stripe.param.checkout.SessionCreateParams;
import eu.kpgtb.shop.data.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity(name = "product_field")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ProductFieldEntity extends BaseEntity {
    String label;
    boolean optional;
    @Enumerated(EnumType.STRING)
    SessionCreateParams.CustomField.Type type;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "field_id")
    List<ProductFieldOptionEntity> options;

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
                                        this.options.stream().map(ProductFieldOptionEntity::getStripeObject).toList()
                                ).build()
                ).build();
    }
}
