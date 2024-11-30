package antifraud.service;

import antifraud.dto.request.FeedbackRequestDTO;
import antifraud.dto.response.FeedbackResponseDTO;
import antifraud.dto.transaction.TransactionRequestDTO;
import antifraud.dto.transaction.TransactionResponseDTO;
import antifraud.enums.TransactionType;
import antifraud.model.Transaction;
import antifraud.repo.StolenCardRepo;
import antifraud.repo.SuspiciousIpRepo;
import antifraud.repo.TransactionRepo;
import antifraud.service.utils.ValidationUtil;
import antifraud.validation.transaction.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static antifraud.constants.ConstantsUtil.updateTransactionLimit;
import static antifraud.service.utils.ValidationUtil.*;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepo transactionRepo;
    private final SuspiciousIpRepo suspiciousIpRepo;
    private final StolenCardRepo stolenCardRepo;

    public ResponseEntity<TransactionResponseDTO> addTransaction(TransactionRequestDTO transactionDTO) {
        if (!isValidTransactionInput(transactionDTO)) {
            return ResponseEntity.badRequest().build();
        }

        List<String> reasonsForRejection = new ArrayList<>();
        String type = reviewTransaction(transactionDTO, reasonsForRejection);

        Transaction transaction = transactionDTO.getTransaction();
        transaction.setResult(type);
        transactionRepo.save(transaction);

        String info = reasonsForRejection.isEmpty() ? "none" : reasonsForRejection.stream().sorted().collect(Collectors.joining(", "));
        return ResponseEntity.ok(new TransactionResponseDTO(type, info));
    }

    private String reviewTransaction(TransactionRequestDTO dto, List<String> reasons) {
        List<TransactionValidator> validators = List.of(
                new SuspiciousIpValidator(suspiciousIpRepo),
                new StolenCardValidator(stolenCardRepo),
                new IpCorrelationValidator(transactionRepo),
                new RegionCorrelationValidator(transactionRepo),
                new AmountValidator()
        );

        TransactionType type = TransactionType.ALLOWED;

        for (TransactionValidator validator : validators) {
            type = validator.validate(dto, reasons, type);
        }

        return type.toString();
    }

    public ResponseEntity<FeedbackResponseDTO> addFeedback(FeedbackRequestDTO feedbackDTO) {
        Optional<Transaction> transactionOptional = transactionRepo.getById(feedbackDTO.getTransactionId());

        if (transactionOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Transaction transaction = transactionOptional.get();

        if (transaction.getFeedback() != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        String feedback = feedbackDTO.getFeedback();
        if (transaction.getResult().equals(feedback)) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }

        updateTransactionLimit(feedback, transaction);

        transaction.setFeedback(feedback);
        transactionRepo.save(transaction);

        return ResponseEntity.ok(new FeedbackResponseDTO(transaction));
    }

    public ResponseEntity<?> getHistory() {
        List<Transaction> transactions = transactionRepo.findAllByOrderByIdAsc();

        if (transactions.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<FeedbackResponseDTO> transactionDTOs = transactions.stream()
                .map(FeedbackResponseDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(transactionDTOs);
    }

    public ResponseEntity<?> getHistoryByNumber(String number) {
        if (!ValidationUtil.isValidCardNumber(number)) {
            return ResponseEntity.badRequest().build();
        }

        List<Transaction> transactions = transactionRepo.findAllByNumberOrderByIdAsc(number);

        if (transactions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<FeedbackResponseDTO> transactionDTOs = transactions.stream()
                .map(FeedbackResponseDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(transactionDTOs);
    }
}
