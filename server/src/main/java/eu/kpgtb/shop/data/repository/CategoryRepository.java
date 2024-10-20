package eu.kpgtb.shop.data.repository;

import eu.kpgtb.shop.data.entity.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Integer> {
}
