package eu.kpgtb.shop.data.repository.product;

import eu.kpgtb.shop.data.entity.product.ApiEntity;
import org.springframework.data.repository.CrudRepository;

public interface ApiRepository extends CrudRepository<ApiEntity,Integer> {
}
