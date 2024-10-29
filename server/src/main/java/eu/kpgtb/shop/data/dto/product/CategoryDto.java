package eu.kpgtb.shop.data.dto.product;

import eu.kpgtb.shop.data.entity.product.CategoryEntity;
import eu.kpgtb.shop.data.entity.product.ProductEntity;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CategoryDto  {
    @Nullable private final Integer id;
    private final String name;
    private final String description;
    private final String nameInUrl;

    private final List<Integer> productsId;
    private final List<ProductDto> products;

    public CategoryDto(CategoryEntity entity) {
        this(entity, new ArrayList<>(),"");
    }
    public CategoryDto(CategoryEntity entity, List<String> expands, String path) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.nameInUrl = entity.getNameInUrl();

        this.productsId = entity.getProducts().stream().map(ProductEntity::getId).toList();
        this.products = expands.contains(path + "products") ?
                entity.getProducts().stream().map(product -> new ProductDto(product,expands,path+"products.")).toList() : new ArrayList<>();
    }
}
