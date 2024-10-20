package eu.kpgtb.shop.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Entity(name = "product")
public class ProductEntity extends BaseEntity{

    String name;
    String description;
    String image;

    String currency;
    double price;

    String stripeId;
    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    public ProductDisplay getDisplay() {
        return new ProductDisplay(this);
    }

    public record ProductDisplay(int id, String name, String description, String image, String currency, double price, int categoryId) {
        public ProductDisplay(ProductEntity product) {
            this(
                    product.getId(),
                    product.name,
                    product.description,
                    product.image,
                    product.currency,
                    product.price,
                    product.category.getId()
            );
        }
    }
}
