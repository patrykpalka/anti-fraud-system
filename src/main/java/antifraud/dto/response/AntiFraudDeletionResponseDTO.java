package antifraud.dto.response;

import antifraud.model.StolenCard;
import antifraud.model.SuspiciousIp;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AntiFraudDeletionResponseDTO {
    private String status;

    public AntiFraudDeletionResponseDTO(SuspiciousIp suspiciousIp) {
        this.status = "IP " + suspiciousIp.getIp() + " successfully removed!";
    }

    public AntiFraudDeletionResponseDTO(StolenCard stolenCard) {
        this.status = "Card " + stolenCard.getNumber() + " successfully removed!";
    }
}
