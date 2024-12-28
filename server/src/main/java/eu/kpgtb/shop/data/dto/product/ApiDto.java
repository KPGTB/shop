package eu.kpgtb.shop.data.dto.product;

import eu.kpgtb.shop.data.entity.product.ApiEntity;
import eu.kpgtb.shop.data.entity.product.RconEntity;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ApiDto {
    private final int id;
    private final String displayName;
    private final String url;

    public ApiDto(ApiEntity entity) {
        this(entity, new ArrayList<>(),"");
    }
    public ApiDto(ApiEntity entity, List<String> expands, String path) {
        this.id = entity.getId();
        this.displayName = entity.getDisplayName();
        this.url = entity.getUrl();
    }
}
