package antifraud.dto.response;

import antifraud.model.StolenCard;
import antifraud.model.SuspiciousIp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AntiFraudDeletionResponseDTO<T> {
    @JsonIgnore
    @NotBlank
    private T entity;

    private String status;

    public AntiFraudDeletionResponseDTO(T entity) {
        this.entity = entity;
        if (entity instanceof SuspiciousIp) {
            this.status = "IP " + ((SuspiciousIp) entity).getIp() + " successfully removed!";
        } else if (entity instanceof StolenCard) {
            this.status = "Card " + ((StolenCard) entity).getNumber() + " successfully removed!";
        } else {
            throw new IllegalArgumentException("Unsupported entity type for status message");
        }
    }
}
