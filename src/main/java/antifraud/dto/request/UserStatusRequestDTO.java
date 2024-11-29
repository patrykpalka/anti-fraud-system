package antifraud.dto.request;

import antifraud.dto.base.BaseUserDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class UserStatusRequestDTO extends BaseUserDTO {
    @NotBlank
    @Pattern(regexp = "LOCK|UNLOCK")
    private String operation;
}
