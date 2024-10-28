package eu.kpgtb.shop.data.dto.order;

import eu.kpgtb.shop.data.dto.product.ProductFieldDto;
import eu.kpgtb.shop.data.entity.order.OrderFieldEntity;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class OrderFieldDto  {
    private final int id;
    private final int fieldId;
    private final ProductFieldDto field;
    private final String value;

    public OrderFieldDto(OrderFieldEntity entity) {
        this(entity, new ArrayList<>(), "");
    }
    public OrderFieldDto(OrderFieldEntity entity, List<String> expands, String path) {
        this.id = entity.getId();
        this.value = entity.getValue();

        this.fieldId = entity.getField().getId();
        this.field = expands.contains(path + "field") ? new ProductFieldDto(entity.getField(),expands, path+"field.") : null;
    }
}
