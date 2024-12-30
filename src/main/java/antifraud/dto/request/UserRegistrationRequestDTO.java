package antifraud.dto.request;

import antifraud.model.AppUser;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
public class UserRegistrationRequestDTO {

    @NotBlank (message = "Username cannot be empty")
    private String username;

    @NotBlank (message = "Name cannot be empty")
    private String name;

    @NotBlank (message = "Password cannot be empty")
    private String password;

    public AppUser toEntity(PasswordEncoder passwordEncoder) {
        return new AppUser(name, username, passwordEncoder.encode(password));
    }
}
