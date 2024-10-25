package eu.kpgtb.shop.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Entity(name = "user")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class UserEntity extends BaseEntity{
    private String email;
    private String password;
    private String verificationToken;
    private boolean active;

    @Enumerated(EnumType.STRING) private UserRole role;

    @Getter
    public enum UserRole {
        CUSTOMER("ROLE_CUSTOMER"),
        BUSINESS("ROLE_BUSINESS");

        private final String authority;
        UserRole(String authority) {
            this.authority = authority;
        }
    }
}
