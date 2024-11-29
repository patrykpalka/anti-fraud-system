package antifraud.dto.request;

import antifraud.dto.base.BaseUserWithNameDTO;
import antifraud.model.AppUser;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class UserRegistrationRequestDTO extends BaseUserWithNameDTO {
    @NotBlank
    private String password;

    public AppUser toEntity(PasswordEncoder passwordEncoder) {
        return new AppUser(name, username, passwordEncoder.encode(password));
    }
}
