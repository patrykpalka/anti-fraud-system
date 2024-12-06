package antifraud.dto.request;

import antifraud.validation.annotation.ValidFeedback;
import lombok.Data;

@Data
public class FeedbackRequestDTO {
    private long transactionId;

    @ValidFeedback
    private String feedback;
}
