package antifraud.dto.request;

import antifraud.dto.base.BaseUserDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class UserRoleRequestDTO extends BaseUserDTO {
    @NotBlank
    private String role;
}
