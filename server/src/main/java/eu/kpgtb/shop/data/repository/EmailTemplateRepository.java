package eu.kpgtb.shop.data.repository;

import eu.kpgtb.shop.data.entity.EmailTemplateEntity;
import org.springframework.data.repository.CrudRepository;

public interface EmailTemplateRepository extends CrudRepository<EmailTemplateEntity,Integer> {
    EmailTemplateEntity findByType(String type);
    boolean existsByType(String type);
}
