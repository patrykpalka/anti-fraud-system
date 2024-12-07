package antifraud.dto.response;

import antifraud.model.RemovableEntity;
import lombok.Data;

@Data
public class AntiFraudDeletionResponseDTO<T extends RemovableEntity> {
    private String status;

    public AntiFraudDeletionResponseDTO(T entity) {
        this.status = entity.deletionMessage();
    }
}
