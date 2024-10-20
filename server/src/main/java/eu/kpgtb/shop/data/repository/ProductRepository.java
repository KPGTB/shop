package eu.kpgtb.shop.data.repository;

import eu.kpgtb.shop.data.entity.ProductEntity;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<ProductEntity, Integer> {
}
