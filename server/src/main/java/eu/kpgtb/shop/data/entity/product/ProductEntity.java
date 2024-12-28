package eu.kpgtb.shop.data.entity.product;

import eu.kpgtb.shop.data.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity(name = "product")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ProductEntity extends BaseEntity {
    String name;
    String nameInUrl;
    @Lob
    String description;
    String image;

    String currency;
    double price;

    String taxCode;
    double displayTax;

    String stripeId;
    @ManyToOne
    @JoinColumn(name = "category_id")
    CategoryEntity category;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    List<ProductFieldEntity> fields;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="product_id")
    List<ProductActionEntity> actions;
}
