package antifraud.controller;

import antifraud.dto.request.*;
import antifraud.dto.response.*;
import antifraud.dto.transaction.TransactionRequestDTO;
import antifraud.dto.transaction.TransactionResponseDTO;
import antifraud.model.StolenCard;
import antifraud.model.SuspiciousIp;
import antifraud.service.AntiFraudService;
import antifraud.service.TransactionService;
import antifraud.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class Controller {

    private final TransactionService transactionService;
    private final UserService userService;
    private final AntiFraudService antiFraudService;

    @PostMapping("/api/antifraud/transaction")
    public ResponseEntity<TransactionResponseDTO> addTransaction(@Valid @RequestBody TransactionRequestDTO transaction) {
        return transactionService.addTransaction(transaction);
    }

    @PostMapping("/api/auth/user")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationRequestDTO registration) {
        return userService.registerUser(registration);
    }

    @GetMapping("/api/auth/list")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/api/auth/user/{username}")
    public ResponseEntity<UserDeletionResponseDTO> deleteUser(@PathVariable String username) {
        return userService.deleteUser(username);
    }

    @PutMapping("/api/auth/role")
    public ResponseEntity<UserResponseDTO> changeRole(@Valid @RequestBody UserRoleRequestDTO roleRequest) {
        return userService.changeRole(roleRequest);
    }

    @PutMapping("/api/auth/access")
    public ResponseEntity<OperationResponseDTO> changeLockedStatus(@Valid @RequestBody UserStatusRequestDTO statusRequest) {
        return userService.changeLockedStatus(statusRequest);
    }

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
        return antiFraudService.removeStolenCard();
    }

    @PutMapping("/api/antifraud/transaction")
    public ResponseEntity<FeedbackResponseDTO> addFeedback(@Valid @RequestBody FeedbackRequestDTO addFeedback) {
        return transactionService.addFeedback(addFeedback);
    }

    @GetMapping("/api/antifraud/history")
    public ResponseEntity<?> getHistory() {
        return transactionService.getHistory();
    }

    @GetMapping("/api/antifraud/history/{number}")
    public ResponseEntity<?> getHistoryByNumber(@PathVariable("number") String number) {
        return transactionService.getHistoryByNumber(number);
    }
}
