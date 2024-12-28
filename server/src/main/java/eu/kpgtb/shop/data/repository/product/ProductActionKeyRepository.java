package eu.kpgtb.shop.data.repository.product;

import eu.kpgtb.shop.data.entity.product.ProductActionKeyEntity;
import org.springframework.data.repository.CrudRepository;

public interface ProductActionKeyRepository extends CrudRepository<ProductActionKeyEntity, Integer> {
    ProductActionKeyEntity findByType(String type);
}
