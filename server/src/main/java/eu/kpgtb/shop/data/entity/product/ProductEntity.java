package eu.kpgtb.shop.data.entity.product;

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
@Entity(name = "product")
public class ProductEntity extends BaseEntity {

    String name;
    String nameInUrl;
    @Lob String description;
    String image;

    String currency;
    double price;
    String taxCode;

    String stripeId;
    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    List<ProductField> fields;

    public ProductDisplay getDisplay() {
        return new ProductDisplay(this);
    }

    public record ProductDisplay(int id, String name, String nameInUrl,String description, String image, String currency, double price,String taxCode, int categoryId, List<ProductField> fields) {
        public ProductDisplay(ProductEntity product) {
            this(
                    product.getId(),
                    product.name,
                    product.nameInUrl,
                    product.description,
                    product.image,
                    product.currency,
                    product.price,
                    product.taxCode,
                    product.category.getId(),
                    product.fields
            );
        }
    }
}
