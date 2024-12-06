package antifraud.dto.request;

import antifraud.model.StolenCard;
import antifraud.validation.annotation.ValidCardNumber;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StolenCardRequestDTO {
    @NotBlank
    @ValidCardNumber
    String number;

    public StolenCard toStolenCard() {
        return new StolenCard(number);
    }
}
