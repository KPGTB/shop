package eu.kpgtb.shop.data.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class EmailTemplateDto  {
    private final int id;
    private final String type;
    private final String subject;
    private final String content;

    public EmailTemplateDto(EmailTemplateDto entity) {
        this(entity, new ArrayList<>(),"");
    }
    public EmailTemplateDto(EmailTemplateDto entity, List<String> expands, String path) {
        this.id = entity.getId();
        this.type = entity.getType();
        this.subject = entity.getSubject();
        this.content = entity.getContent();
    }
}
