package antifraud.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserStatusRequestDTO {

    @NotBlank
    private String username;

    @NotBlank
    @Pattern(regexp = "LOCK|UNLOCK")
    private String operation;
}
