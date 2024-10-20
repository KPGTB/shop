package eu.kpgtb.shop.auth;

import eu.kpgtb.shop.data.entity.UserEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Date;
import java.util.Arrays;
import java.util.Collection;

@Getter
public class User implements UserDetails {
    private UserEntity entity;

    private PrivateUserInfo privateUserInfo;
    private PublicUserInfo publicUserInfo;

    public User(UserEntity entity) {
        this.entity = entity;
        this.privateUserInfo = new PrivateUserInfo(entity.getEmail(),entity.getRole(), entity.getName(),entity.getSurname(),entity.getGender(),entity.getBirthDate());
        this.publicUserInfo = new PublicUserInfo(entity.getName());
    }

    @Override
    public String getPassword() {
        return this.entity.getPassword();
    }
    @Override
    public String getUsername() {
        return this.entity.getEmail();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority(entity.getRole().getAuthority()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }


    public record PrivateUserInfo(String email, UserEntity.UserRole role, String name, String surname, UserEntity.Gender gender, Date birthDate) {}
    public record PublicUserInfo(String name) {}
}
