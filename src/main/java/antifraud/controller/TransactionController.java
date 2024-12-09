package antifraud.controller;

import antifraud.dto.request.FeedbackRequestDTO;
import antifraud.dto.response.FeedbackResponseDTO;
import antifraud.dto.request.TransactionRequestDTO;
import antifraud.dto.response.TransactionResponseDTO;
import antifraud.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/api/antifraud/transaction")
    public ResponseEntity<TransactionResponseDTO> addTransaction(@Valid @RequestBody TransactionRequestDTO transaction) {
        return transactionService.addTransaction(transaction);
    }

    @PutMapping("/api/antifraud/transaction")
    public ResponseEntity<FeedbackResponseDTO> addFeedback(@Valid @RequestBody FeedbackRequestDTO addFeedback) {
        return transactionService.addFeedback(addFeedback);
    }

    @GetMapping("/api/antifraud/history")
    public ResponseEntity<List<FeedbackResponseDTO>> getHistory() {
        return transactionService.getHistory();
    }

    @GetMapping("/api/antifraud/history/{number}")
    public ResponseEntity<List<FeedbackResponseDTO>> getHistoryByNumber(@PathVariable("number") String number) {
        return transactionService.getHistoryByNumber(number);
    }
}
