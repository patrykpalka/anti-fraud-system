package antifraud.dto.response;

import antifraud.model.AppUser;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OperationResponseDTO {
    private String status;

    public static OperationResponseDTO ofLockStatus(AppUser user) {
        OperationResponseDTO response = new OperationResponseDTO();
        String status = user.isLocked() ? "locked" : "unlocked";
        response.setStatus(String.format("User %s %s!", user.getUsername(), status));
        return response;
    }
}
