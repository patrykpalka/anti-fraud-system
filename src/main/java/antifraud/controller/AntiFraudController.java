package antifraud.controller;

import antifraud.dto.request.StolenCardRequestDTO;
import antifraud.dto.request.SuspiciousIpRequestDTO;
import antifraud.dto.response.AntiFraudDeletionResponseDTO;
import antifraud.model.StolenCard;
import antifraud.model.SuspiciousIp;
import antifraud.service.AntiFraudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AntiFraudController {

    private final AntiFraudService antiFraudService;

    @PostMapping("/api/antifraud/suspicious-ip")
    public ResponseEntity<SuspiciousIp> addSuspiciousIp(@Valid @RequestBody SuspiciousIpRequestDTO suspiciousIpRequestDTO) {
        return antiFraudService.addSuspiciousIp(suspiciousIpRequestDTO);
    }

    @DeleteMapping("/api/antifraud/suspicious-ip/{ip}")
    public ResponseEntity<AntiFraudDeletionResponseDTO> removeSuspiciousIp(@PathVariable String ip) {
        return antiFraudService.removeSuspiciousIp(ip);
    }

    @GetMapping("/api/antifraud/suspicious-ip")
    public ResponseEntity<List<SuspiciousIp>> getSuspiciousIps() {
        return antiFraudService.getSuspiciousIps();
    }

    @PostMapping("/api/antifraud/stolencard")
    public ResponseEntity<StolenCard> addStolenCard(@Valid @RequestBody StolenCardRequestDTO stolenCardRequestDTO) {
        return antiFraudService.addStolenCard(stolenCardRequestDTO);
    }

    @DeleteMapping("/api/antifraud/stolencard/{number}")
    public ResponseEntity<AntiFraudDeletionResponseDTO> removeStolenCard(@PathVariable("number") String number) {
        return antiFraudService.removeStolenCard(number);
    }

    @GetMapping("/api/antifraud/stolencard")
    public ResponseEntity<List<StolenCard>> getStolenCards() {
        return antiFraudService.getStolenCards();
    }
}