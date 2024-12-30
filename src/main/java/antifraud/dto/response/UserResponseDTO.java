package antifraud.dto.response;

import antifraud.model.AppUser;
import lombok.Data;

@Data
public class UserResponseDTO {

    private String username;
    private String name;
    private long id;
    private String role;

    public UserResponseDTO(AppUser user) {
        this.id = user.getId();
        this.name = user.getName();
        this.username = user.getUsername();
        this.role = user.getRoles().iterator().next().toString();
    }
}
