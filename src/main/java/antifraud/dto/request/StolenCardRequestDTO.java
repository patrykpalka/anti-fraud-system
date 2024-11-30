package antifraud.dto.request;

import antifraud.model.StolenCard;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StolenCardRequestDTO {
    @NotBlank
    @Pattern(regexp = "\\d{16}")
    String number;

    public StolenCard toStolenCard() {
        return new StolenCard(number);
    }
}
