package eu.kpgtb.shop.data.dto.product;

import eu.kpgtb.shop.data.entity.product.ProductFieldEntity;
import eu.kpgtb.shop.data.entity.product.ProductFieldOptionEntity;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ProductFieldDto  {
    private final int id;
    private final String label;
    private final boolean optional;
    private final String type;

    private final List<Integer> optionsId;
    private final List<ProductFieldOptionDto> options;

    public ProductFieldDto(ProductFieldEntity entity) {
        this(entity, new ArrayList<>(),"");
    }
    public ProductFieldDto(ProductFieldEntity entity, List<String> expands, String path) {
        this.id = entity.getId();
        this.label = entity.getLabel();
        this.optional = entity.isOptional();
        this.type = entity.getType().name();

        this.optionsId = entity.getOptions().stream().map(ProductFieldOptionEntity::getId).toList();
        this.options = expands.contains(path+"options") ?
                entity.getOptions().stream().map(option -> new ProductFieldOptionDto(option,expands,path+"options.")).toList() : new ArrayList<>();
    }
}
