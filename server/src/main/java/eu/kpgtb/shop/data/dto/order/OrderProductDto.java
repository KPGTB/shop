package eu.kpgtb.shop.data.dto.order;

import eu.kpgtb.shop.data.dto.product.ProductDto;
import eu.kpgtb.shop.data.entity.order.OrderFieldEntity;
import eu.kpgtb.shop.data.entity.order.OrderProductEntity;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class OrderProductDto  {
    private final int id;
    private final int productId;
    private final ProductDto product;
    private final int quantity;
    private final List<Integer> fieldsId;
    private final List<OrderFieldDto> fields;

    public OrderProductDto(OrderProductEntity entity) {
        this(entity, new ArrayList<>(), "");
    }
    public OrderProductDto(OrderProductEntity entity, List<String> expands, String path) {
        this.id = entity.getId();
        this.quantity = entity.getQuantity();

        this.productId = entity.getProduct().getId();
        this.product = expands.contains(path + "product") ?
                new ProductDto(entity.getProduct(),expands, path + "product.") : null;

        this.fieldsId = entity.getFields().stream().map(OrderFieldEntity::getId).toList();
        this.fields = expands.contains(path+"fields") ?
                entity.getFields().stream().map(field -> new OrderFieldDto(field,expands, path + "fields.")).toList() : new ArrayList<>();
    }
}
