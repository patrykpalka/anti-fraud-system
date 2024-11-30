package antifraud.validation.transaction;

import antifraud.dto.transaction.TransactionRequestDTO;
import antifraud.model.Transaction;
import antifraud.repo.TransactionRepo;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RegionCorrelationValidator extends AbstractCorrelationValidator {

    public RegionCorrelationValidator(TransactionRepo transactionRepo) {
        super(transactionRepo);
    }

    @Override
    protected Set<String> extractDistinctValues(List<Transaction> transactions, TransactionRequestDTO dto) {
        return transactions.stream()
                .map(Transaction::getRegion)
                .filter(region -> !region.equals(dto.getRegion())) // Exclude current region
                .collect(Collectors.toSet());
    }

    @Override
    protected String getReasonKey() {
        return "region-correlation";
    }
}
