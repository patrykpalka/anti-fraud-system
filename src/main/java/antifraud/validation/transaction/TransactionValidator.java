package antifraud.validation.transaction;

import antifraud.dto.request.TransactionRequestDTO;
import antifraud.enums.TransactionType;

import java.util.List;

public interface TransactionValidator {

    TransactionType validate(TransactionRequestDTO dto, List<String> reasons, TransactionType currentType);
}
