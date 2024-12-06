package antifraud.dto.response;

import lombok.Data;

@Data
public class UserDeletionResponseDTO {
    private String username;
    private String status;

    public static UserDeletionResponseDTO ofDeletion(String username) {
        UserDeletionResponseDTO response = new UserDeletionResponseDTO();
        response.setUsername(username);
        response.setStatus("Deleted successfully!");
        return response;
    }
}
