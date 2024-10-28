package eu.kpgtb.shop.data.dto.product;

import eu.kpgtb.shop.data.entity.product.ProductFieldOptionEntity;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ProductFieldOptionDto  {
    private final int id;
    private final String label;
    private final String value;

    public ProductFieldOptionDto(ProductFieldOptionEntity entity) {
        this(entity, new ArrayList<>(),"");
    }
    public ProductFieldOptionDto(ProductFieldOptionEntity entity, List<String> expands, String path) {
        this.id = entity.getId();
        this.label = entity.getLabel();
        this.value = entity.getValue();
    }
}
