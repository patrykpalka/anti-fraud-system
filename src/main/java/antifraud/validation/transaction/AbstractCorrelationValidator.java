package antifraud.validation.transaction;

import antifraud.dto.request.TransactionRequestDTO;
import antifraud.enums.TransactionType;
import antifraud.model.Transaction;
import antifraud.repo.TransactionRepo;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public abstract class AbstractCorrelationValidator implements TransactionValidator {

    protected final TransactionRepo transactionRepo;

    public AbstractCorrelationValidator(TransactionRepo transactionRepo) {
        this.transactionRepo = transactionRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionType validate(TransactionRequestDTO dto, List<String> reasons, TransactionType currentType) {
        // Fetch recent transactions within the past hour
        LocalDateTime requestTime = dto.getDate();
        LocalDateTime oneHourAgo = requestTime.minusHours(1);
        List<Transaction> recentTransactions = transactionRepo
                .findAllByDateGreaterThanEqualAndDateLessThanAndNumber(oneHourAgo, requestTime, dto.getNumber());

        // Extract and validate field-specific correlations
        Set<String> distinctValues = extractDistinctValues(recentTransactions, dto);
        return evaluateCorrelations(distinctValues, reasons, currentType);
    }

    protected abstract Set<String> extractDistinctValues(List<Transaction> transactions, TransactionRequestDTO dto);

    private TransactionType evaluateCorrelations(Set<String> distinctValues, List<String> reasons, TransactionType currentType) {
        if (distinctValues.size() > 2) {
            reasons.add(getReasonKey());
            return TransactionType.PROHIBITED;
        } else if (distinctValues.size() == 2) {
            reasons.add(getReasonKey());
            return currentType == TransactionType.ALLOWED ? TransactionType.MANUAL_PROCESSING : currentType;
        }
        return currentType;
    }

    protected abstract String getReasonKey();
}
