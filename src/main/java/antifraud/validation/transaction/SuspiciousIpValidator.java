package antifraud.validation.transaction;

import antifraud.dto.transaction.TransactionRequestDTO;
import antifraud.enums.TransactionType;
import antifraud.repo.SuspiciousIpRepo;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
public class SuspiciousIpValidator implements TransactionValidator {

    private final SuspiciousIpRepo suspiciousIpRepo;

    @Override
    @Transactional(readOnly = true)
    public TransactionType validate(TransactionRequestDTO dto, List<String> reasons, TransactionType currentType) {
        if (suspiciousIpRepo.findAllIps().contains(dto.getIp())) {
            currentType = TransactionType.PROHIBITED;
            reasons.add("ip");
        }

        return currentType;
    }
}
