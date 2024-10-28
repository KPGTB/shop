package eu.kpgtb.shop.data.repository.order;

import eu.kpgtb.shop.data.entity.order.OrderFieldEntity;
import org.springframework.data.repository.CrudRepository;

public interface OrderProductFieldRepository extends CrudRepository<OrderFieldEntity, Integer> {
}
