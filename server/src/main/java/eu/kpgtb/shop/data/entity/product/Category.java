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
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Entity
public class Category extends BaseEntity {
    private String name;
    @Lob private String description;
    private String nameInUrl;

    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE)
    private List<ProductEntity> products;

    public CategoryDisplay getDisplay() {
        return new CategoryDisplay(this);
    }

    public record CategoryDisplay(int id, String name, String description, String nameInUrl, List<ProductEntity.ProductDisplay> products) {
        public CategoryDisplay(Category category) {
            this(
                    category.getId(),
                    category.name,
                    category.description,
                    category.nameInUrl,
                    category.products
                            .stream()
                            .map(ProductEntity.ProductDisplay::new)
                            .collect(Collectors.toList())
            );
        }
    }
}
