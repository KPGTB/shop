package eu.kpgtb.shop.data.entity.product;

import eu.kpgtb.shop.data.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity(name = "category")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class CategoryEntity extends BaseEntity {
    String name;
    @Lob
    String description;
    String nameInUrl;

    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE)
    List<ProductEntity> products;
}
