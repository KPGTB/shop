package eu.kpgtb.shop.data.dto.product;

import eu.kpgtb.shop.data.entity.product.RconEntity;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RconDto {
    private final int id;
    private final String displayName;
    private final String host;

    public RconDto(RconEntity entity) {
        this(entity, new ArrayList<>(),"");
    }
    public RconDto(RconEntity entity, List<String> expands, String path) {
        this.id = entity.getId();
        this.displayName = entity.getDisplayName();
        this.host = entity.getHost();
    }
}
