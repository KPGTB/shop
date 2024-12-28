package eu.kpgtb.shop.data.entity.product;

import eu.kpgtb.shop.data.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "product_action")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ProductActionEntity extends BaseEntity {
    @Enumerated(EnumType.STRING)
    ActionType type;
    @Lob
    String data;

    public enum ActionType {
        FILE, // Data: file key (name of uploaded file from /file/uploadFile)
        RCON, // Fields: rcon_id, command
        KEY,  // Data: type (type of key from db)
        API // Fields: api_id, method, content (params or body)
    }
}
