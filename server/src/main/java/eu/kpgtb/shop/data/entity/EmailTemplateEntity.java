package eu.kpgtb.shop.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity(name = "email_template")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class EmailTemplateEntity extends BaseEntity{
    String type;
    String subject;
    @Lob
    String content;

    public enum CommonTemplateType {
        ACCOUNT_ACTIVATION,FORGOT_PASSWORD
    }
}
