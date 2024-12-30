package antifraud.dto.response;

import antifraud.model.Transaction;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FeedbackResponseDTO {

    private long transactionId;
    private long amount;
    private String ip;
    private String number;
    private String region;
    private LocalDateTime date;
    private String result;
    private String feedback;

    public FeedbackResponseDTO(Transaction transaction) {
        this.transactionId = transaction.getId();
        this.amount = transaction.getAmount();
        this.ip = transaction.getIp();
        this.number = transaction.getNumber();
        this.region = transaction.getRegion();
        this.date = transaction.getDate();
        this.result = transaction.getResult();
        this.feedback = transaction.getFeedback() == null ? "" : transaction.getFeedback();
    }
}
