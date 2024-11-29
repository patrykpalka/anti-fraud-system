package antifraud.service;

import antifraud.constants.Constants;
import antifraud.dto.request.FeedbackRequestDTO;
import antifraud.dto.response.FeedbackResponseDTO;
import antifraud.dto.transaction.TransactionRequestDTO;
import antifraud.dto.transaction.TransactionResponseDTO;
import antifraud.enums.RegionNames;
import antifraud.enums.TransactionType;
import antifraud.model.Transaction;
import antifraud.repo.StolenCardRepo;
import antifraud.repo.SuspiciousIpRepo;
import antifraud.repo.TransactionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final SuspiciousIpRepo suspiciousIpRepo;
    private final StolenCardRepo stolenCardRepo;
    private final TransactionRepo transactionRepo;

    public ResponseEntity<TransactionResponseDTO> addTransaction(TransactionRequestDTO transactionDTO) {
        // Validate input
        if (transactionDTO.getAmount() <= 0 ||
                VerificationUtil.isCardNumberInvalid(transactionDTO.getNumber()) ||
                !isValidRegion(transactionDTO.getRegion())) {
            return ResponseEntity.badRequest().build();
        }

        // Initialize variables
        TransactionType type = TransactionType.ALLOWED;
        List<String> reasons = new ArrayList<>();

        // Check suspicious IPs and stolen cards
        if (suspiciousIpRepo.findAllIps().contains(transactionDTO.getIp())) {
            type = TransactionType.PROHIBITED;
            reasons.add("ip");
        }

        if (stolenCardRepo.findAllCardNumbers().contains(transactionDTO.getNumber())) {
            type = TransactionType.PROHIBITED;
            reasons.add("card-number");
        }

        // Analyze transaction history for correlations
        LocalDateTime requestTime = transactionDTO.getDate();
        LocalDateTime oneHourAgo = requestTime.minusHours(1);
        List<Transaction> recentTransactions = transactionRepo
                .findAllByDateGreaterThanEqualAndDateLessThanAndNumber(oneHourAgo, requestTime, transactionDTO.getNumber());

        // IP correlation
        Set<String> distinctIps = recentTransactions.stream()
                .map(Transaction::getIp)
                .filter(ip -> !ip.equals(transactionDTO.getIp()))  // Exclude current IP
                .collect(Collectors.toSet());

        if (distinctIps.size() > 2) {
            type = TransactionType.PROHIBITED;
            reasons.add("ip-correlation");
        } else if (distinctIps.size() == 2) {
            if (type != TransactionType.PROHIBITED) {
                type = TransactionType.MANUAL_PROCESSING;
            }
            reasons.add("ip-correlation");
        }

        // Region correlation
        Set<String> distinctRegions = recentTransactions.stream()
                .map(Transaction::getRegion)
                .filter(region -> !region.equals(transactionDTO.getRegion()))  // Exclude current region
                .collect(Collectors.toSet());

        if (distinctRegions.size() > 2) {
            type = TransactionType.PROHIBITED;
            reasons.add("region-correlation");
        } else if (distinctRegions.size() == 2 && type == TransactionType.ALLOWED) {
            type = TransactionType.MANUAL_PROCESSING;
            reasons.add("region-correlation");
        }

        // Check transaction amount
        if (transactionDTO.getAmount() > Constants.MAX_MANUAL_PROCESSING) {
            type = TransactionType.PROHIBITED;
            reasons.add("amount");
        } else if (transactionDTO.getAmount() > Constants.MAX_ALLOWED && reasons.isEmpty()) {
            type = TransactionType.MANUAL_PROCESSING;
            reasons.add("amount");
        }

        // Save transaction
        Transaction transaction = transactionDTO.getTransaction();
        transaction.setResult(type.toString());
        transactionRepo.save(transaction);

        // Prepare response
        String info = reasons.isEmpty() ? "none" : reasons.stream().sorted().collect(Collectors.joining(", "));
        return ResponseEntity.ok(new TransactionResponseDTO(type.toString(), info));
    }

    private boolean isValidRegion(String region) {
        return Arrays.stream(RegionNames.values()).anyMatch(r -> r.name().equals(region));
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

        if (transaction.getResult().equals(feedbackDTO.getFeedback())) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }

        TransactionType feedbackTransactionType = TransactionType.valueOf(feedbackDTO.getFeedback());
        TransactionType resultTransactionType = TransactionType.valueOf(transaction.getResult());
        long amount = transaction.getAmount();

        if (feedbackTransactionType == TransactionType.ALLOWED) {
            if (resultTransactionType == TransactionType.MANUAL_PROCESSING) {
                Constants.MAX_ALLOWED = calculateNewLimit(Constants.MAX_ALLOWED, amount, true);
            } else if (resultTransactionType == TransactionType.PROHIBITED) {
                Constants.MAX_ALLOWED = calculateNewLimit(Constants.MAX_ALLOWED, amount, true);
                Constants.MAX_MANUAL_PROCESSING = calculateNewLimit(Constants.MAX_MANUAL_PROCESSING, amount, true);
            }
        } else if (feedbackTransactionType == TransactionType.MANUAL_PROCESSING) {
            if (resultTransactionType == TransactionType.ALLOWED) {
                Constants.MAX_ALLOWED = calculateNewLimit(Constants.MAX_ALLOWED, amount, false);
            } else if (resultTransactionType == TransactionType.PROHIBITED) {
                Constants.MAX_MANUAL_PROCESSING = calculateNewLimit(Constants.MAX_MANUAL_PROCESSING, amount, true);
            }
        } else if (feedbackTransactionType == TransactionType.PROHIBITED) {
            if (resultTransactionType == TransactionType.ALLOWED) {
                Constants.MAX_ALLOWED = calculateNewLimit(Constants.MAX_ALLOWED, amount, false);
                Constants.MAX_MANUAL_PROCESSING = calculateNewLimit(Constants.MAX_MANUAL_PROCESSING, amount, false);
            } else if (resultTransactionType == TransactionType.MANUAL_PROCESSING) {
                Constants.MAX_MANUAL_PROCESSING = calculateNewLimit(Constants.MAX_MANUAL_PROCESSING, amount, false);
            }
        }

        transaction.setFeedback(feedbackDTO.getFeedback());
        transactionRepo.save(transaction);

        return ResponseEntity.ok(new FeedbackResponseDTO(transaction));
    }

    private long calculateNewLimit(long currentLimit, long valueFromTransaction, boolean increase) {
        double factor = increase ? 1 : -1;
        double updatedValue = 0.8 * currentLimit + factor * 0.2 * valueFromTransaction;
        return (long) Math.ceil(updatedValue);
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
        if (!number.matches("\\d{16}")) {
            return ResponseEntity.badRequest().build();
        }

        if (VerificationUtil.isCardNumberInvalid(number)) {
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
