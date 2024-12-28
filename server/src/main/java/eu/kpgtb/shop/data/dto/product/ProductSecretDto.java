package eu.kpgtb.shop.data.dto.product;

import eu.kpgtb.shop.data.entity.product.ProductSecretEntity;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ProductSecretDto {
    private final int id;
    private final String code;

    public ProductSecretDto(ProductSecretEntity entity) {
        this(entity, new ArrayList<>(),"");
    }
    public ProductSecretDto(ProductSecretEntity entity, List<String> expands, String path) {
        this.id = entity.getId();
        this.code = entity.getCode();
    }
}
