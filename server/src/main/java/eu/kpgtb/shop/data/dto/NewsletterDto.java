package eu.kpgtb.shop.data.dto;

import eu.kpgtb.shop.data.entity.NewsletterEntity;
import eu.kpgtb.shop.data.entity.UserEntity;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class NewsletterDto {
    private final int id;
    private final String email;
    private final String token;
    private final boolean active;

    public NewsletterDto(NewsletterEntity entity) {
        this(entity, new ArrayList<>(),"");
    }
    public NewsletterDto(NewsletterEntity entity, List<String> expands, String path) {
        this.id = entity.getId();
        this.email = entity.getEmail();
        this.token = entity.getToken();
        this.active = entity.isActive();
    }
}
