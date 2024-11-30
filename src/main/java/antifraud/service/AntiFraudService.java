package antifraud.service;

import antifraud.dto.request.SuspiciousIpRequestDTO;
import antifraud.dto.request.StolenCardRequestDTO;
import antifraud.dto.response.AntiFraudDeletionResponseDTO;
import antifraud.model.StolenCard;
import antifraud.model.SuspiciousIp;
import antifraud.repo.StolenCardRepo;
import antifraud.repo.SuspiciousIpRepo;
import antifraud.service.utils.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static antifraud.service.utils.ValidationUtil.*;

@Service
@RequiredArgsConstructor
public class AntiFraudService {

    private final SuspiciousIpRepo suspiciousIpRepo;
    private final StolenCardRepo stolenCardRepo;

    public ResponseEntity<SuspiciousIp> addSuspiciousIp(SuspiciousIpRequestDTO suspiciousIpRequestDTO) {
        String ip = suspiciousIpRequestDTO.getIp();

        if (!isValidIp(ip)) {
            return ResponseEntity.badRequest().build();
        }

        if (suspiciousIpRepo.findByIp(ip).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        SuspiciousIp suspiciousIp = suspiciousIpRequestDTO.toSuspiciousIp();
        suspiciousIpRepo.save(suspiciousIp);

        return ResponseEntity.ok(suspiciousIp);
    }

    public ResponseEntity<StolenCard> addStolenCard(StolenCardRequestDTO stolenCardRequestDTO) {
        String number = stolenCardRequestDTO.getNumber();

        if (!isValidCardNumber(number)) {
            return ResponseEntity.badRequest().build();
        }

        if (stolenCardRepo.findByNumber(number).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        StolenCard stolenCard = stolenCardRequestDTO.toStolenCard();
        stolenCardRepo.save(stolenCard);

        return ResponseEntity.ok(stolenCard);
    }

    public ResponseEntity<List<SuspiciousIp>> getSuspiciousIps() {
        return ResponseEntity.ok(suspiciousIpRepo.findAllByOrderByIdAsc());
    }

    public ResponseEntity<List<StolenCard>> getStolenCards() {
        return ResponseEntity.ok(stolenCardRepo.findAllByOrderByIdAsc());
    }

    public ResponseEntity<AntiFraudDeletionResponseDTO> removeSuspiciousIp(String requestIp) {
        if (!isValidIp(requestIp)) {
            return ResponseEntity.badRequest().build();
        }

        Optional<SuspiciousIp> suspiciousIpOptional = suspiciousIpRepo.findByIp(requestIp);

        if (suspiciousIpOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        SuspiciousIp suspiciousIp = suspiciousIpOptional.get();
        suspiciousIpRepo.delete(suspiciousIp);

        return ResponseEntity.ok(new AntiFraudDeletionResponseDTO(suspiciousIp));
    }

    public ResponseEntity<AntiFraudDeletionResponseDTO> removeStolenCard(String number) {
        if (!isValidCardNumber(number)) {
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
}
