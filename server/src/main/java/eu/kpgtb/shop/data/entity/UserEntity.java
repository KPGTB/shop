package eu.kpgtb.shop.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "user")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class UserEntity extends BaseEntity{
    String email;
    String password;
    String verificationToken;
    boolean active;

    @Enumerated(EnumType.STRING)
    UserRole role;

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
