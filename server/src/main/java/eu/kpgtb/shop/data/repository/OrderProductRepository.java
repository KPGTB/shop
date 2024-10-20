package eu.kpgtb.shop.data.repository;

import eu.kpgtb.shop.data.entity.OrderProductEntity;
import org.springframework.data.repository.CrudRepository;

public interface OrderProductRepository extends CrudRepository<OrderProductEntity, Integer> {
}
