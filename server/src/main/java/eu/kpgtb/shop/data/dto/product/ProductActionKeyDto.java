package eu.kpgtb.shop.data.dto.product;

import eu.kpgtb.shop.data.entity.product.ProductActionKeyEntity;
import eu.kpgtb.shop.data.entity.product.RconEntity;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ProductActionKeyDto {
    private final int id;
    private final String type;
    private final String key;

    public ProductActionKeyDto(ProductActionKeyEntity entity) {
        this(entity, new ArrayList<>(),"");
    }
    public ProductActionKeyDto(ProductActionKeyEntity entity, List<String> expands, String path) {
        this.id = entity.getId();
        this.type = entity.getType();
        this.key = entity.getKey();
    }
}
