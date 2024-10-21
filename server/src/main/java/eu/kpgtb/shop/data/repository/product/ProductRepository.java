package eu.kpgtb.shop.data.repository.product;

import eu.kpgtb.shop.data.entity.product.ProductEntity;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<ProductEntity, Integer> {
}
