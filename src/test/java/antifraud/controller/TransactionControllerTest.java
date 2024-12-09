package antifraud.controller;

import antifraud.dto.request.FeedbackRequestDTO;
import antifraud.dto.request.TransactionRequestDTO;
import antifraud.dto.response.FeedbackResponseDTO;
import antifraud.dto.response.TransactionResponseDTO;
import antifraud.model.Transaction;
import antifraud.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private TransactionRequestDTO validTransactionRequest;
    private Transaction validTransaction;

    @BeforeEach
    void setUp() {
        // Common setup for all tests
        validTransactionRequest = createValidTransactionRequest();
        validTransaction = createValidTransaction();
    }

    @Test
    @DisplayName("Should successfully add transaction and return ALLOWED response")
    void shouldAddTransactionSuccessfully() {
        // Arrange
        TransactionResponseDTO expectedResponse = new TransactionResponseDTO("ALLOWED", "none");
        when(transactionService.addTransaction(validTransactionRequest))
                .thenReturn(ResponseEntity.ok(expectedResponse));

        // Act
        ResponseEntity<TransactionResponseDTO> response =
                transactionController.addTransaction(validTransactionRequest);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedResponse, response.getBody());
        verify(transactionService).addTransaction(validTransactionRequest);
    }

    @Test
    @DisplayName("Should successfully add feedback to a transaction")
    void shouldAddFeedbackSuccessfully() {
        // Arrange
        FeedbackRequestDTO feedbackRequest = createValidFeedbackRequest();
        FeedbackResponseDTO expectedResponse = new FeedbackResponseDTO(validTransaction);

        when(transactionService.addFeedback(feedbackRequest))
                .thenReturn(ResponseEntity.ok(expectedResponse));

        // Act
        ResponseEntity<FeedbackResponseDTO> response =
                transactionController.addFeedback(feedbackRequest);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedResponse, response.getBody());
        verify(transactionService).addFeedback(feedbackRequest);
    }

    @Test
    @DisplayName("Should retrieve transaction history successfully")
    void shouldRetrieveTransactionHistory() {
        // Arrange
        List<Transaction> transactions = createMockTransactions();
        List<FeedbackResponseDTO> expectedHistory = transactions.stream()
                .map(FeedbackResponseDTO::new)
                .collect(Collectors.toList());

        when(transactionService.getHistory())
                .thenReturn(ResponseEntity.ok(expectedHistory));

        // Act
        ResponseEntity<?> response = transactionController.getHistory();

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedHistory, response.getBody());
        verify(transactionService).getHistory();
    }

    @Test
    @DisplayName("Should retrieve transaction history for specific card number")
    void shouldRetrieveTransactionHistoryByCardNumber() {
        // Arrange
        String cardNumber = "4000000000000002";
        FeedbackResponseDTO feedbackResponse = new FeedbackResponseDTO(validTransaction);
        List<FeedbackResponseDTO> expectedHistory = List.of(feedbackResponse);

        when(transactionService.getHistoryByNumber(cardNumber))
                .thenReturn(ResponseEntity.ok(expectedHistory));

        // Act
        ResponseEntity<List<FeedbackResponseDTO>> response =
                transactionController.getHistoryByNumber(cardNumber);

        // Assert
            assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedHistory, response.getBody());
        verify(transactionService).getHistoryByNumber(cardNumber);
    }

    // Helper methods for creating test data
    private TransactionRequestDTO createValidTransactionRequest() {
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAmount(100);
        request.setIp("192.168.1.1");
        request.setNumber("4000000000000002");
        request.setRegion("EAU");
        request.setDate(LocalDateTime.now());
        return request;
    }

    private Transaction createValidTransaction() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(100);
        transaction.setIp("192.168.1.1");
        transaction.setNumber("4000000000000002");
        transaction.setRegion("EAU");
        transaction.setDate(LocalDateTime.now());
        transaction.setResult("ALLOWED");
        return transaction;
    }

    private FeedbackRequestDTO createValidFeedbackRequest() {
        FeedbackRequestDTO request = new FeedbackRequestDTO();
        request.setTransactionId(1L);
        request.setFeedback("ALLOWED");
        return request;
    }

    private List<Transaction> createMockTransactions() {
        Transaction transaction1 = createValidTransaction();
        Transaction transaction2 = new Transaction();
        transaction2.setId(2L);
        transaction2.setAmount(200);
        transaction2.setIp("192.168.1.2");
        transaction2.setNumber("4000000000000003");
        transaction2.setRegion("NA");
        transaction2.setDate(LocalDateTime.now());
        transaction2.setResult("BLOCKED");

        return List.of(transaction1, transaction2);
    }
}
