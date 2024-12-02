package antifraud.dto.transaction;

import antifraud.model.Transaction;
import antifraud.validation.annotation.ValidCardNumber;
import antifraud.validation.annotation.ValidIp;
import antifraud.validation.annotation.ValidRegion;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestDTO {
    @Size(min = 1)
    private long amount;

    @NotBlank
    @ValidIp
    private String ip;

    @NotBlank
    @ValidCardNumber
    private String number;

    @NotBlank
    @ValidRegion
    private String region;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;

    public Transaction getTransaction() {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setIp(ip);
        transaction.setNumber(number);
        transaction.setRegion(region);
        transaction.setDate(date);
        return transaction;
    }
}
