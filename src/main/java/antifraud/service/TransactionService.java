package antifraud.service;

import antifraud.dto.request.FeedbackRequestDTO;
import antifraud.dto.response.FeedbackResponseDTO;
import antifraud.dto.request.TransactionRequestDTO;
import antifraud.dto.response.TransactionResponseDTO;
import antifraud.enums.TransactionType;
import antifraud.exception.*;
import antifraud.model.Transaction;
import antifraud.repo.StolenCardRepo;
import antifraud.repo.SuspiciousIpRepo;
import antifraud.repo.TransactionRepo;
import antifraud.validation.annotation.ValidCardNumber;
import antifraud.validation.transaction.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static antifraud.service.utils.ConstantsUtil.updateTransactionLimit;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepo transactionRepo;
    private final SuspiciousIpRepo suspiciousIpRepo;
    private final StolenCardRepo stolenCardRepo;

    @Transactional
    public ResponseEntity<TransactionResponseDTO> addTransaction(TransactionRequestDTO transactionDTO) {
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

    @Transactional
    public ResponseEntity<FeedbackResponseDTO> addFeedback(FeedbackRequestDTO feedbackDTO) {
        Transaction transaction = transactionRepo.getById(feedbackDTO.getTransactionId())
                .orElseThrow(() -> new NotFoundException("Transaction not found"));

        if (transaction.getFeedback() != null) {
            throw new ConflictException("Transaction already has feedback");
        }

        String feedback = feedbackDTO.getFeedback();
        if (transaction.getResult().equals(feedback)) {
            throw new UnprocessableEntityException("Result and feedback cannot be the same");
        }

        updateTransactionLimit(feedback, transaction);

        transaction.setFeedback(feedback);
        transactionRepo.save(transaction);

        return ResponseEntity.ok(new FeedbackResponseDTO(transaction));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getHistory() {
        List<Transaction> transactions = transactionRepo.findAllByOrderByIdAsc();

        return ResponseEntity.ok(transactions.isEmpty() ? Collections.emptyList() : transactions.stream()
                .map(FeedbackResponseDTO::new)
                .collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<List<FeedbackResponseDTO>> getHistoryByNumber(@Valid @ValidCardNumber String number) {
        List<Transaction> transactions = transactionRepo.findAllByNumberOrderByIdAsc(number);

        if (transactions.isEmpty()) {
            throw new NotFoundException("Transaction not found");
        }

        List<FeedbackResponseDTO> transactionDTOs = transactions.stream()
                .map(FeedbackResponseDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(transactionDTOs);
    }
}
