package antifraud.dto.base;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
public abstract class BaseUserWithNameDTO extends BaseUserDTO {
    @NotBlank
    protected String name;
}
