package eu.kpgtb.shop.serivce;

import eu.kpgtb.shop.data.entity.EmailTemplateEntity;
import eu.kpgtb.shop.data.repository.EmailTemplateRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.SneakyThrows;

import java.io.File;
import java.io.InputStream;
import java.util.*;

@Getter @Builder
public class EmailData {
    @Singular("to") private List<String> to;
    private boolean useBcc;
    private String subject;
    @Singular  private List<File> attachments;
    @Singular  private Map<String, InputStream> isAttachments;

    private String html;

    private String template;
    @Singular private Map<String,String> placeholders;
    private EmailTemplateRepository emailTemplateRepository;

    public String getSubject() {
        if(this.subject != null) return this.subject;
        if(this.template == null) return null;

        EmailTemplateEntity entity = emailTemplateRepository.findByType(this.template);
        String result =entity != null ? entity.getSubject() : "";

        for (Map.Entry<String, String> entry : this.placeholders.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }

    @SneakyThrows
    public String getContent() {
        if(this.html != null) return this.html;
        if(this.template == null) return null;

        EmailTemplateEntity entity = this.emailTemplateRepository.findByType(this.template);
        String content = entity != null ? entity.getContent() : "";
        for (Map.Entry<String, String> entry : this.placeholders.entrySet()) {
            content = content.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }

        return content;
    }


}