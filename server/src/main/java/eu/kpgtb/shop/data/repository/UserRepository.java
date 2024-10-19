package eu.kpgtb.shop.data.repository;

import eu.kpgtb.shop.data.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Integer> {
    UserEntity findByEmail(String email);
    boolean existsByEmail(String email);
}
