package eu.kpgtb.shop.data.repository.product;

import eu.kpgtb.shop.data.entity.product.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Integer> {
}
