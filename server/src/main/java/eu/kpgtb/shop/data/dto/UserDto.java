package eu.kpgtb.shop.data.dto;

import eu.kpgtb.shop.data.entity.UserEntity;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class UserDto  {
    private final int id;
    private final String email;
    private final boolean active;
    private final String role;

    public UserDto(UserEntity entity) {
        this(entity, new ArrayList<>(),"");
    }
    public UserDto(UserEntity entity, List<String> expands, String path) {
        this.id = entity.getId();
        this.email = entity.getEmail();
        this.active = entity.isActive();
        this.role = entity.getRole().name();
    }
}
