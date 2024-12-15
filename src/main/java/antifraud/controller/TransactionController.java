package antifraud.controller;

import antifraud.dto.request.FeedbackRequestDTO;
import antifraud.dto.request.TransactionRequestDTO;
import antifraud.dto.response.FeedbackResponseDTO;
import antifraud.dto.response.TransactionResponseDTO;
import antifraud.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/api/antifraud/transaction")
    public ResponseEntity<TransactionResponseDTO> addTransaction(
            @Valid @RequestBody TransactionRequestDTO transaction, Authentication authentication) {
        return transactionService.addTransaction(transaction, authentication);
    }

    @PutMapping("/api/antifraud/transaction")
    public ResponseEntity<FeedbackResponseDTO> addFeedback(@Valid @RequestBody FeedbackRequestDTO addFeedback, Authentication authentication) {
        return transactionService.addFeedback(addFeedback, authentication);
    }

    @GetMapping("/api/antifraud/history")
    public ResponseEntity<List<FeedbackResponseDTO>> getHistory(Pageable pageable) {
        return transactionService.getHistory(pageable);
    }

    @GetMapping("/api/antifraud/history/{number}")
    public ResponseEntity<List<FeedbackResponseDTO>> getHistoryByNumber(@PathVariable("number") String number, Pageable pageable) {
        return transactionService.getHistoryByNumber(number, pageable);
    }
}
