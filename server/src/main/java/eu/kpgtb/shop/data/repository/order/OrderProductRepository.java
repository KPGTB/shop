package eu.kpgtb.shop.data.repository.order;

import eu.kpgtb.shop.data.entity.order.OrderProductEntity;
import org.springframework.data.repository.CrudRepository;

public interface OrderProductRepository extends CrudRepository<OrderProductEntity, Integer> {
}
