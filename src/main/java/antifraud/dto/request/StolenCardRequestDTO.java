package antifraud.dto.request;

import antifraud.model.StolenCard;
import antifraud.validation.annotation.ValidCardNumber;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StolenCardRequestDTO {
    @NotBlank
    @ValidCardNumber
    String number;

    public StolenCard toStolenCard() {
        return new StolenCard(number);
    }
}
