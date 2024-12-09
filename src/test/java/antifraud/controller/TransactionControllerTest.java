package antifraud.controller;

import antifraud.dto.request.FeedbackRequestDTO;
import antifraud.dto.request.TransactionRequestDTO;
import antifraud.dto.response.FeedbackResponseDTO;
import antifraud.dto.response.TransactionResponseDTO;
import antifraud.model.Transaction;
import antifraud.repo.TransactionRepo;
import antifraud.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private TransactionRepo transactionRepo;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        transactionController = new TransactionController(transactionService);
    }

    @Test
    void testAddTransaction_Success() {
        TransactionRequestDTO requestDTO = createMockTransactionRequestDTO();
        TransactionResponseDTO responseDTO = new TransactionResponseDTO("ALLOWED", "none");

        when(transactionService.addTransaction(requestDTO)).thenReturn(ResponseEntity.ok(responseDTO));

        ResponseEntity<TransactionResponseDTO> response = transactionController.addTransaction(requestDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
        verify(transactionService, times(1)).addTransaction(requestDTO);
    }

    @Test
    void testAddFeedback_Success() {
        // Create a mock transaction
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(100);
        transaction.setIp("192.168.1.1");
        transaction.setNumber("4000000000000002");
        transaction.setRegion("EAU");
        transaction.setDate(LocalDateTime.now());
        transaction.setResult("ALLOWED");

        // Create a mock request DTO
        FeedbackRequestDTO requestDTO = createMockFeedbackRequestDTO();

        // Create the expected response DTO
        FeedbackResponseDTO responseDTO = new FeedbackResponseDTO(transaction);

        // Mock the service to return the response DTO
        when(transactionService.addFeedback(requestDTO)).thenReturn(ResponseEntity.ok(responseDTO));

        // Call the controller method
        ResponseEntity<FeedbackResponseDTO> response = transactionController.addFeedback(requestDTO);

        // Assertions
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
        verify(transactionService, times(1)).addFeedback(requestDTO);
    }

    @Test
    void testGetHistory_WithData() {
        // Create mock transactions
        Transaction transaction1 = new Transaction();
        transaction1.setId(1L);
        transaction1.setAmount(100);
        transaction1.setIp("192.168.1.1");
        transaction1.setNumber("4000000000000002");
        transaction1.setRegion("EAU");
        transaction1.setDate(LocalDateTime.now());
        transaction1.setResult("ALLOWED");

        Transaction transaction2 = new Transaction();
        transaction2.setId(2L);
        transaction2.setAmount(200);
        transaction2.setIp("192.168.1.2");
        transaction2.setNumber("4000000000000003");
        transaction2.setRegion("NA");
        transaction2.setDate(LocalDateTime.now());
        transaction2.setResult("BLOCKED");

        List<Transaction> transactions = List.of(transaction1, transaction2);

        // Mock the transactionRepo to return the mock transactions
        when(transactionService.getHistory()).thenReturn(ResponseEntity.ok(
                transactions.stream()
                        .map(FeedbackResponseDTO::new)
                        .collect(Collectors.toList())
        ));

        // Call the controller method
        ResponseEntity<?> response = transactionController.getHistory();

        // Assertions
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(transactions.stream().map(FeedbackResponseDTO::new).collect(Collectors.toList()), response.getBody());
        verify(transactionService, times(1)).getHistory();
    }

    @Test
    void testGetHistoryByNumber_Success() {
        String cardNumber = "4000000000000002";

        // Create a mock transaction
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(100);
        transaction.setIp("192.168.1.1");
        transaction.setNumber(cardNumber);
        transaction.setRegion("EAU");
        transaction.setDate(LocalDateTime.now());
        transaction.setResult("ALLOWED");

        // Use the transaction to create a valid FeedbackResponseDTO
        FeedbackResponseDTO feedbackResponseDTO = new FeedbackResponseDTO(transaction);
        List<FeedbackResponseDTO> history = Collections.singletonList(feedbackResponseDTO);

        // Mock the service response
        when(transactionService.getHistoryByNumber(cardNumber)).thenReturn(ResponseEntity.ok(history));

        // Call the controller method
        ResponseEntity<List<FeedbackResponseDTO>> response = transactionController.getHistoryByNumber(cardNumber);

        // Assertions
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(history, response.getBody());
        verify(transactionService, times(1)).getHistoryByNumber(cardNumber);
    }

    private TransactionRequestDTO createMockTransactionRequestDTO() {
        TransactionRequestDTO dto = new TransactionRequestDTO();
        dto.setAmount(100);
        dto.setIp("192.168.1.1");
        dto.setNumber("4000000000000002");
        dto.setRegion("EAU");
        dto.setDate(LocalDateTime.now());
        return dto;
    }

    private FeedbackRequestDTO createMockFeedbackRequestDTO() {
        FeedbackRequestDTO dto = new FeedbackRequestDTO();
        dto.setTransactionId(1L);
        dto.setFeedback("ALLOWED");
        return dto;
    }
}
