package antifraud.controller;

import antifraud.dto.request.FeedbackRequestDTO;
import antifraud.dto.request.TransactionRequestDTO;
import antifraud.dto.response.FeedbackResponseDTO;
import antifraud.dto.response.TransactionResponseDTO;
import antifraud.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Transaction APIs", description = "APIs for managing transactions and feedback.")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/api/antifraud/transaction")
    @Operation(summary = "Submit Transaction", description = "Analyzes a transaction for fraud detection.")
    public ResponseEntity<TransactionResponseDTO> addTransaction(
            @Valid @RequestBody @Parameter(description = "Transaction details") TransactionRequestDTO transaction,
            Authentication authentication) {
        return transactionService.addTransaction(transaction, authentication);
    }

    @PutMapping("/api/antifraud/transaction")
    @Operation(summary = "Add Feedback", description = "Adds feedback to a transaction.")
    public ResponseEntity<FeedbackResponseDTO> addFeedback(
            @Valid @RequestBody @Parameter(description = "Feedback details") FeedbackRequestDTO feedback,
            Authentication authentication) {
        return transactionService.addFeedback(feedback, authentication);
    }

    @GetMapping("/api/antifraud/history")
    @Operation(summary = "Transaction History", description = "Fetches transaction feedback history.")
    public ResponseEntity<List<FeedbackResponseDTO>> getHistory(
            @Parameter(description = "Pagination details") Pageable pageable) {
        return transactionService.getHistory(pageable);
    }

    @GetMapping("/api/antifraud/history/{number}")
    @Operation(summary = "History by Card Number", description = "Fetches transaction history by card number.")
    public ResponseEntity<List<FeedbackResponseDTO>> getHistoryByNumber(
            @PathVariable("number") @Parameter(description = "Card number for filtering history") String number,
            Pageable pageable) {
        return transactionService.getHistoryByNumber(number, pageable);
    }
}
