package eu.kpgtb.shop.data.repository.order;

import eu.kpgtb.shop.data.entity.order.OrderEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderRepository extends CrudRepository<OrderEntity, Integer> {
    Optional<OrderEntity> findByInvoiceNumber(String invoiceNumber);
}
