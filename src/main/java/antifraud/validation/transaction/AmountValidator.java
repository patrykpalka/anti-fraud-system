package antifraud.validation.transaction;

import antifraud.constants.Constants;
import antifraud.dto.transaction.TransactionRequestDTO;
import antifraud.enums.TransactionType;

import java.util.List;

public class AmountValidator implements TransactionValidator {
    @Override
    public TransactionType validate(TransactionRequestDTO dto, List<String> reasons, TransactionType currentType) {
        if (dto.getAmount() > Constants.MAX_MANUAL_PROCESSING) {
            currentType = TransactionType.PROHIBITED;
            reasons.add("amount");
        } else if (dto.getAmount() > Constants.MAX_ALLOWED && reasons.isEmpty()) {
            currentType = TransactionType.MANUAL_PROCESSING;
            reasons.add("amount");
        }

        return currentType;
    }
}
