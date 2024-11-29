package antifraud.service;

import antifraud.dto.request.SuspiciousIpRequestDTO;
import antifraud.dto.request.StolenCardRequestDTO;
import antifraud.dto.response.AntiFraudDeletionResponseDTO;
import antifraud.model.StolenCard;
import antifraud.model.SuspiciousIp;
import antifraud.repo.StolenCardRepo;
import antifraud.repo.SuspiciousIpRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AntiFraudService {

    private final SuspiciousIpRepo suspiciousIpRepo;
    private final StolenCardRepo stolenCardRepo;

    public ResponseEntity<SuspiciousIp> addSuspiciousIp(SuspiciousIpRequestDTO suspiciousIpRequestDTO) {
        Optional<SuspiciousIp> suspiciousIpOptional = suspiciousIpRepo.findByIp(suspiciousIpRequestDTO.getIp());

        if (suspiciousIpOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        SuspiciousIp suspiciousIp = new SuspiciousIp(suspiciousIpRequestDTO.getIp());
        suspiciousIpRepo.save(suspiciousIp);

        return ResponseEntity.ok(suspiciousIp);
    }

    public ResponseEntity<AntiFraudDeletionResponseDTO> removeSuspiciousIp(String requestIp) {
        String validIp = "^(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\\." +
                "(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\\." +
                "(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\\." +
                "(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])$";

        if (!requestIp.matches(validIp)) {
            return ResponseEntity.badRequest().build();
        }

        Optional<SuspiciousIp> ipOptional = suspiciousIpRepo.findByIp(requestIp);

        if (ipOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        SuspiciousIp suspiciousIp = ipOptional.get();
        suspiciousIpRepo.delete(suspiciousIp);

        return ResponseEntity.ok(new AntiFraudDeletionResponseDTO(suspiciousIp));
    }

    public ResponseEntity<List<SuspiciousIp>> getSuspiciousIps() {
        List<SuspiciousIp> suspiciousIpList = suspiciousIpRepo.findAllByOrderByIdAsc();
        return ResponseEntity.ok(suspiciousIpList);
    }

    public ResponseEntity<StolenCard> addStolenCard(StolenCardRequestDTO stolenCardRequestDTO) {
        Optional<StolenCard> stolenCardOptional = stolenCardRepo.findByNumber(stolenCardRequestDTO.getNumber());

        if (stolenCardOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        StolenCard stolenCard = new StolenCard(stolenCardRequestDTO.getNumber());

        if (VerificationUtil.isCardNumberInvalid(stolenCard.getNumber())) {
            return ResponseEntity.badRequest().build();
        }

        stolenCardRepo.save(stolenCard);
        return ResponseEntity.ok(stolenCard);
    }

    public ResponseEntity<AntiFraudDeletionResponseDTO> removeStolenCard(String number) {
        if (!number.matches("\\d{16}")) {
            return ResponseEntity.badRequest().build();
        }

        if (VerificationUtil.isCardNumberInvalid(number)) {
            return ResponseEntity.badRequest().build();
        }

        Optional<StolenCard> stolenCardOptional = stolenCardRepo.findByNumber(number);
        if (stolenCardOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        StolenCard stolenCard = stolenCardOptional.get();
        stolenCardRepo.delete(stolenCard);

        return ResponseEntity.ok(new AntiFraudDeletionResponseDTO(stolenCard));
    }

    public ResponseEntity<List<StolenCard>> removeStolenCard() {
        List<StolenCard> stolenCardList = stolenCardRepo.findAllByOrderByIdAsc();
        return ResponseEntity.ok(stolenCardList);
    }
}
