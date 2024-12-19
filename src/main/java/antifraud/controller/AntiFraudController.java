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
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Tag(name = "AntiFraudController", description = "APIs for managing suspicious IPs and stolen cards.")
public class AntiFraudController {

    private final AntiFraudService antiFraudService;

    @PostMapping("/api/antifraud/suspicious-ip")
    @Operation(summary = "Add Suspicious IP", description = "Marks an IP address as suspicious. The IP must be in valid IPv4 format.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Suspicious IP added successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuspiciousIp.class))),
            @ApiResponse(responseCode = "400", description = "Invalid IP address format"),
            @ApiResponse(responseCode = "409", description = "IP address already marked as suspicious")
    })
    public ResponseEntity<SuspiciousIp> addSuspiciousIp(
            @Valid @RequestBody @Parameter(description = "Details of the suspicious IP", required = true) SuspiciousIpRequestDTO suspiciousIpRequestDTO) {
        return antiFraudService.addSuspiciousIp(suspiciousIpRequestDTO);
    }

    @GetMapping("/api/antifraud/suspicious-ip")
    @Operation(summary = "Get All Suspicious IPs", description = "Retrieves a list of all suspicious IPs.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of suspicious IPs",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SuspiciousIp.class))))
    })
    public ResponseEntity<List<SuspiciousIp>> getSuspiciousIps() {
        return antiFraudService.getSuspiciousIps();
    }

    @DeleteMapping("/api/antifraud/suspicious-ip/{ip}")
    @Operation(summary = "Remove Suspicious IP", description = "Deletes an IP address from the suspicious list if it exists.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Suspicious IP removed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AntiFraudDeletionResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Suspicious IP not found")
    })
    public ResponseEntity<AntiFraudDeletionResponseDTO<SuspiciousIp>> removeSuspiciousIp(
            @ValidIp @PathVariable @Parameter(description = "IP address to remove", required = true, example = "192.168.0.1") String ip) {
        return antiFraudService.removeSuspiciousIp(ip);
    }

    @PostMapping("/api/antifraud/stolencard")
    @Operation(summary = "Add Stolen Card", description = "Marks a card as stolen. The card number must be valid.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stolen card added successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StolenCard.class))),
            @ApiResponse(responseCode = "400", description = "Invalid card number"),
            @ApiResponse(responseCode = "409", description = "Card already marked as stolen")
    })
    public ResponseEntity<StolenCard> addStolenCard(
            @Valid @RequestBody @Parameter(description = "Details of the stolen card", required = true) StolenCardRequestDTO stolenCardRequestDTO) {
        return antiFraudService.addStolenCard(stolenCardRequestDTO);
    }

    @GetMapping("/api/antifraud/stolencard")
    @Operation(summary = "Get All Stolen Cards", description = "Retrieves a list of all stolen cards.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of stolen cards",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = StolenCard.class))))
    })
    public ResponseEntity<List<StolenCard>> getStolenCards() {
        return antiFraudService.getStolenCards();
    }

    @DeleteMapping("/api/antifraud/stolencard/{number}")
    @Operation(summary = "Remove Stolen Card", description = "Deletes a card number from the stolen cards list if it exists.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stolen card removed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AntiFraudDeletionResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Stolen card not found")
    })
    public ResponseEntity<AntiFraudDeletionResponseDTO<StolenCard>> removeStolenCard(
            @ValidCardNumber @PathVariable @Parameter(description = "Card number to remove", required = true, example = "1234567812345678") String number) {
        return antiFraudService.removeStolenCard(number);
    }
}
