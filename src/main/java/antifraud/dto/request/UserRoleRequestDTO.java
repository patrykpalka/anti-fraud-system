package antifraud.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRoleRequestDTO {
    @NotBlank
    private String username;

    @NotBlank
    private String role;
}
