package antifraud.validation.transaction;

import antifraud.dto.transaction.TransactionRequestDTO;
import antifraud.model.Transaction;
import antifraud.repo.TransactionRepo;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class IpCorrelationValidator extends AbstractCorrelationValidator {

    public IpCorrelationValidator(TransactionRepo transactionRepo) {
        super(transactionRepo);
    }

    @Override
    protected Set<String> extractDistinctValues(List<Transaction> transactions, TransactionRequestDTO dto) {
        return transactions.stream()
                .map(Transaction::getIp)
                .filter(ip -> !ip.equals(dto.getIp())) // Exclude current IP
                .collect(Collectors.toSet());
    }

    @Override
    protected String getReasonKey() {
        return "ip-correlation";
    }
}
