package antifraud.validation.transaction;

import antifraud.dto.transaction.TransactionRequestDTO;
import antifraud.enums.TransactionType;
import antifraud.repo.StolenCardRepo;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class StolenCardValidator implements TransactionValidator {

    private final StolenCardRepo stolenCardRepo;

    @Override
    public TransactionType validate(TransactionRequestDTO dto, List<String> reasons, TransactionType currentType) {
        if (stolenCardRepo.findAllCardNumbers().contains(dto.getNumber())) {
            currentType = TransactionType.PROHIBITED;
            reasons.add("card-number");
        }

        return currentType;
    }
}
