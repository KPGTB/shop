package eu.kpgtb.shop.data.repository.product;

import eu.kpgtb.shop.data.entity.product.ProductFieldOptionEntity;
import org.springframework.data.repository.CrudRepository;

public interface ProductDropdownOptionRepository extends CrudRepository<ProductFieldOptionEntity, Integer> {
}
