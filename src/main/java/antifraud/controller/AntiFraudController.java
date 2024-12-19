package antifraud.controller;

import antifraud.dto.request.StolenCardRequestDTO;
import antifraud.dto.request.SuspiciousIpRequestDTO;
import antifraud.dto.response.AntiFraudDeletionResponseDTO;
import antifraud.model.StolenCard;
import antifraud.model.SuspiciousIp;
import antifraud.service.AntiFraudService;
import antifraud.validation.annotation.ValidCardNumber;
import antifraud.validation.annotation.ValidIp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Anti-Fraud APIs", description = "APIs for managing suspicious IPs and stolen cards.")
public class AntiFraudController {

    private final AntiFraudService antiFraudService;

    @PostMapping("/api/antifraud/suspicious-ip")
    @Operation(summary = "Add Suspicious IP", description = "Marks an IP address as suspicious.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Suspicious IP added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid IP address format")
    })
    public ResponseEntity<SuspiciousIp> addSuspiciousIp(
            @Valid @RequestBody @Parameter(description = "Suspicious IP details") SuspiciousIpRequestDTO suspiciousIpRequestDTO) {
        return antiFraudService.addSuspiciousIp(suspiciousIpRequestDTO);
    }

    @GetMapping("/api/antifraud/suspicious-ip")
    @Operation(summary = "List Suspicious IPs", description = "Fetches all suspicious IPs.")
    public ResponseEntity<List<SuspiciousIp>> getSuspiciousIps() {
        return antiFraudService.getSuspiciousIps();
    }

    @DeleteMapping("/api/antifraud/suspicious-ip/{ip}")
    @Operation(summary = "Remove Suspicious IP", description = "Deletes an IP address from the suspicious list.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Suspicious IP removed successfully"),
            @ApiResponse(responseCode = "404", description = "IP address not found")
    })
    public ResponseEntity<AntiFraudDeletionResponseDTO<SuspiciousIp>> removeSuspiciousIp(
            @ValidIp @PathVariable @Parameter(description = "Suspicious IP address to delete") String ip) {
        return antiFraudService.removeSuspiciousIp(ip);
    }

    @PostMapping("/api/antifraud/stolencard")
    @Operation(summary = "Add Stolen Card", description = "Marks a card number as stolen.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stolen card added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid card details")
    })
    public ResponseEntity<StolenCard> addStolenCard(
            @Valid @RequestBody @Parameter(description = "Stolen card details") StolenCardRequestDTO stolenCardRequestDTO) {
        return antiFraudService.addStolenCard(stolenCardRequestDTO);
    }

    @GetMapping("/api/antifraud/stolencard")
    @Operation(summary = "List Stolen Cards", description = "Fetches all stolen cards.")
    public ResponseEntity<List<StolenCard>> getStolenCards() {
        return antiFraudService.getStolenCards();
    }

    @DeleteMapping("/api/antifraud/stolencard/{number}")
    @Operation(summary = "Remove Stolen Card", description = "Deletes a stolen card by card number.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stolen card removed successfully"),
            @ApiResponse(responseCode = "404", description = "Card number not found")
    })
    public ResponseEntity<AntiFraudDeletionResponseDTO<StolenCard>> removeStolenCard(
            @ValidCardNumber @PathVariable @Parameter(description = "Card number to delete") String number) {
        return antiFraudService.removeStolenCard(number);
    }
}
