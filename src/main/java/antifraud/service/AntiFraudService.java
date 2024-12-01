package antifraud.service;

import antifraud.dto.request.SuspiciousIpRequestDTO;
import antifraud.dto.request.StolenCardRequestDTO;
import antifraud.dto.response.AntiFraudDeletionResponseDTO;
import antifraud.exception.BadRequestException;
import antifraud.exception.ConflictException;
import antifraud.exception.NotFoundException;
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
            throw new BadRequestException("Invalid IP address");
        }

        if (suspiciousIpRepo.findByIp(ip).isPresent()) {
            throw new ConflictException("This IP address is already in use");
        }

        SuspiciousIp suspiciousIp = suspiciousIpRequestDTO.toSuspiciousIp();
        suspiciousIpRepo.save(suspiciousIp);

        return ResponseEntity.ok(suspiciousIp);
    }

    public ResponseEntity<StolenCard> addStolenCard(StolenCardRequestDTO stolenCardRequestDTO) {
        String number = stolenCardRequestDTO.getNumber();

        if (!isValidCardNumber(number)) {
            throw new BadRequestException("Invalid card number");
        }

        if (stolenCardRepo.findByNumber(number).isPresent()) {
            throw new ConflictException("This number is already in use");
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
            throw new BadRequestException("Invalid IP address");
        }

        Optional<SuspiciousIp> suspiciousIpOptional = suspiciousIpRepo.findByIp(requestIp);

        if (suspiciousIpOptional.isEmpty()) {
            throw new NotFoundException("Suspicious ip not found");
        }

        SuspiciousIp suspiciousIp = suspiciousIpOptional.get();
        suspiciousIpRepo.delete(suspiciousIp);

        return ResponseEntity.ok(new AntiFraudDeletionResponseDTO(suspiciousIp));
    }

    public ResponseEntity<AntiFraudDeletionResponseDTO> removeStolenCard(String number) {
        if (!isValidCardNumber(number)) {
            throw new BadRequestException("Invalid card number");
        }

        Optional<StolenCard> stolenCardOptional = stolenCardRepo.findByNumber(number);

        if (stolenCardOptional.isEmpty()) {
            throw new NotFoundException("Stolen Card not found");
        }

        StolenCard stolenCard = stolenCardOptional.get();
        stolenCardRepo.delete(stolenCard);

        return ResponseEntity.ok(new AntiFraudDeletionResponseDTO(stolenCard));
    }
}
