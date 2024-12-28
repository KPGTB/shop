package eu.kpgtb.shop.data.dto.product;

import eu.kpgtb.shop.data.entity.product.ProductActionEntity;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ProductActionDto {
    private final int id;
    private final ProductActionEntity.ActionType type;
    private final String data;

    public ProductActionDto(ProductActionEntity entity) {
        this(entity, new ArrayList<>(),"");
    }
    public ProductActionDto(ProductActionEntity entity, List<String> expands, String path) {
        this.id = entity.getId();
        this.type = entity.getType();
        this.data = entity.getData();
    }
}
