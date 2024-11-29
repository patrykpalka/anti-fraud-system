package antifraud.dto.response;

import antifraud.dto.base.BaseUserWithNameDTO;
import antifraud.model.AppUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class UserResponseDTO extends BaseUserWithNameDTO {
    private long id;
    private String role;

    public UserResponseDTO(AppUser user) {
        this.id = user.getId();
        this.name = user.getName();
        this.username = user.getUsername();
        this.role = user.getRoles().iterator().next().toString();
    }
}
