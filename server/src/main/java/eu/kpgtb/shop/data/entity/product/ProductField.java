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

    @OneToMany
    @JoinColumn(name = "field_id")
    private List<ProductDropdownOption> options;
}
