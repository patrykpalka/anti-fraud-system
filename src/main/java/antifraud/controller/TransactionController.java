package antifraud.controller;

import antifraud.dto.request.FeedbackRequestDTO;
import antifraud.dto.request.TransactionRequestDTO;
import antifraud.dto.response.FeedbackResponseDTO;
import antifraud.dto.response.TransactionResponseDTO;
import antifraud.service.TransactionService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "TransactionController", description = "APIs for managing transactions and transaction feedback.")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/api/antifraud/transaction")
    @Operation(summary = "Submit Transaction", description = "Analyzes a transaction for fraud detection and returns the result with potential fraud indicators.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction analyzed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid transaction details"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public ResponseEntity<TransactionResponseDTO> addTransaction(
            @Valid @RequestBody @Parameter(description = "Transaction details to analyze", required = true) TransactionRequestDTO transaction,
            @Parameter(hidden = true) Authentication authentication) {
        return transactionService.addTransaction(transaction, authentication);
    }

    @PutMapping("/api/antifraud/transaction")
    @Operation(summary = "Add Transaction Feedback", description = "Adds feedback to a processed transaction for system learning.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Feedback added successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FeedbackResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid feedback details"),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "409", description = "Feedback already exists"),
            @ApiResponse(responseCode = "422", description = "Feedback matches transaction result")
    })
    public ResponseEntity<FeedbackResponseDTO> addFeedback(
            @Valid @RequestBody @Parameter(description = "Feedback details for the transaction", required = true) FeedbackRequestDTO feedback,
            @Parameter(hidden = true) Authentication authentication) {
        return transactionService.addFeedback(feedback, authentication);
    }

    @GetMapping("/api/antifraud/history")
    @Operation(summary = "Get Transaction History", description = "Retrieves paginated transaction history with feedback.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction history retrieved successfully",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FeedbackResponseDTO.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public ResponseEntity<List<FeedbackResponseDTO>> getHistory(
            @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.ASC)
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        return transactionService.getHistory(pageable);
    }

    @GetMapping("/api/antifraud/history/{number}")
    @Operation(summary = "Get Card Transaction History", description = "Retrieves paginated transaction history for a specific card number.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Card transaction history retrieved successfully",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FeedbackResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid card number format"),
            @ApiResponse(responseCode = "404", description = "No transactions found for the card number")
    })
    public ResponseEntity<List<FeedbackResponseDTO>> getHistoryByNumber(
            @PathVariable @Parameter(description = "Card number to get history for", required = true, example = "4000008449433403") String number,
            @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC)
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        return transactionService.getHistoryByNumber(number, pageable);
    }
}
