package eu.kpgtb.shop.data.repository;

import eu.kpgtb.shop.data.entity.EmailTemplate;
import org.springframework.data.repository.CrudRepository;

public interface EmailTemplateRepository extends CrudRepository<EmailTemplate,Integer> {
    EmailTemplate findByType(String type);
    boolean existsByType(String type);
}
