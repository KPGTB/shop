package eu.kpgtb.shop.data.repository;

import eu.kpgtb.shop.data.entity.NewsletterEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface NewsletterRepository extends CrudRepository<NewsletterEntity, Integer> {
    Optional<NewsletterEntity> findByToken(String token);
    Optional<NewsletterEntity> findByEmail(String email);
    List<NewsletterEntity> findAllByActive(boolean active);
}
