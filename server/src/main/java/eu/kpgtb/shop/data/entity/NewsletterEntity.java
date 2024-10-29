package eu.kpgtb.shop.data.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "newsletter")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class NewsletterEntity extends BaseEntity{
    String email;
    String token;
    boolean active;
}
