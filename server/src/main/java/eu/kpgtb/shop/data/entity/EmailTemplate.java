package eu.kpgtb.shop.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@Entity
public class EmailTemplate extends BaseEntity{
    private String type;
    private String subject;
    @Lob private String content;

    public enum CommonTemplateType {
        ACCOUNT_ACTIVATION,FORGOT_PASSWORD
    }
}
