package antifraud.dto.request;

import antifraud.validation.ValidFeedback;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequestDTO {
    private long transactionId;

    @ValidFeedback
    private String feedback;
}
