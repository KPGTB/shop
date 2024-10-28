package eu.kpgtb.shop.data.dto.product;

import eu.kpgtb.shop.data.entity.product.ProductEntity;
import eu.kpgtb.shop.data.entity.product.ProductFieldEntity;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ProductDto  {
    private final int id;
    private final String name;
    private final String nameInUrl;
    private final String description;
    private final String image;

    private final String currency;
    private final double price;
    private final String taxCode;
    private final double displayTax;
    private final double displayPrice;

    private final String stripeId;

    private final int categoryId;
    private final CategoryDto category;

    private final List<Integer> fieldsId;
    private final List<ProductFieldDto> fields;

    public ProductDto(ProductEntity entity) {
        this(entity, new ArrayList<>(),"");
    }
    public ProductDto(ProductEntity entity, List<String> expands, String path) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.nameInUrl = entity.getNameInUrl();
        this.description = entity.getDescription();
        this.image = entity.getImage();
        this.currency = entity.getCurrency();
        this.price = entity.getPrice();
        this.taxCode = entity.getTaxCode();
        this.displayTax = entity.getDisplayTax();
        this.displayPrice = this.price * (1.0 + (this.displayTax / 100.0));
        this.stripeId = entity.getStripeId();

        this.categoryId = entity.getCategory().getId();
        this.category = expands.contains(path+"category") ?
                new CategoryDto(entity.getCategory(),expands,path+"category.") : null;

        this.fieldsId = entity.getFields().stream().map(ProductFieldEntity::getId).toList();
        this.fields = expands.contains(path+"fields") ?
                entity.getFields().stream().map(field -> new ProductFieldDto(field,expands, path+"fields.")).toList() : new ArrayList<>();
    }
}
