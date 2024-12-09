package antifraud.service;

import antifraud.constants.Constants;
import antifraud.dto.request.FeedbackRequestDTO;
import antifraud.dto.request.TransactionRequestDTO;
import antifraud.dto.response.FeedbackResponseDTO;
import antifraud.dto.response.TransactionResponseDTO;
import antifraud.enums.TransactionType;
import antifraud.exception.ConflictException;
import antifraud.exception.NotFoundException;
import antifraud.exception.UnprocessableEntityException;
import antifraud.model.Transaction;
import antifraud.repo.StolenCardRepo;
import antifraud.repo.SuspiciousIpRepo;
import antifraud.repo.TransactionRepo;
import antifraud.service.utils.ConstantsUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepo transactionRepo;

    @Mock
    private SuspiciousIpRepo suspiciousIpRepo;

    @Mock
    private StolenCardRepo stolenCardRepo;

    private TransactionRequestDTO transactionDTO;
    private FeedbackRequestDTO feedbackDTO;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        initTestData();
    }

    private void initTestData() {
        transactionDTO = new TransactionRequestDTO();
        transactionDTO.setAmount(100);
        transactionDTO.setIp("123.45.67.89");
        transactionDTO.setNumber("1234567890123456");
        transactionDTO.setRegion("EAP");
        transactionDTO.setDate(java.time.LocalDateTime.now());

        feedbackDTO = new FeedbackRequestDTO();
        feedbackDTO.setTransactionId(1L);
        feedbackDTO.setFeedback("APPROVED");

        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(100);
        transaction.setIp("123.45.67.89");
        transaction.setNumber("1234567890123456");
        transaction.setRegion("EAP");
        transaction.setDate(java.time.LocalDateTime.now());
    }

    @Test
    void testAddTransactionSuccessfully() {
        // Mock dependencies
        when(transactionRepo.save(any(Transaction.class))).thenReturn(transaction);

        // Execute the service method
        ResponseEntity<TransactionResponseDTO> response = transactionService.addTransaction(transactionDTO);

        // Verify the result
        assertNotNull(response);
        assertEquals(TransactionType.ALLOWED.toString(), response.getBody().getResult());
        verify(transactionRepo, times(1)).save(any(Transaction.class));
    }

    @Test
    void testAddFeedbackTransactionNotFound() {
        // Mock behavior
        when(transactionRepo.findById(1L)).thenReturn(Optional.empty());

        // Execute and verify exception
        assertThrows(NotFoundException.class, () -> transactionService.addFeedback(feedbackDTO));
        verify(transactionRepo, times(1)).findById(1L);
    }

    @Test
    void testAddFeedbackConflict() {
        transaction.setFeedback("APPROVED");
        when(transactionRepo.findById(1L)).thenReturn(Optional.of(transaction));

        assertThrows(ConflictException.class, () -> transactionService.addFeedback(feedbackDTO));
        verify(transactionRepo, times(1)).findById(1L);
        verify(transactionRepo, times(0)).save(any(Transaction.class));
    }

    @Test
    void testAddFeedbackUnprocessableEntity() {
        transaction.setResult("APPROVED");
        when(transactionRepo.findById(1L)).thenReturn(Optional.of(transaction));

        UnprocessableEntityException exception = assertThrows(UnprocessableEntityException.class, () ->
                transactionService.addFeedback(feedbackDTO));

        assertEquals("Result and feedback cannot be the same", exception.getMessage());
        verify(transactionRepo, times(1)).findById(1L);
        verify(transactionRepo, times(0)).save(any(Transaction.class));
    }

    @Test
    void testGetHistory() {
        List<Transaction> transactions = Arrays.asList(new Transaction(), new Transaction());
        when(transactionRepo.findAllByOrderByIdAsc()).thenReturn(transactions);

        ResponseEntity<?> response = transactionService.getHistory();

        assertNotNull(response);
        assertEquals(2, ((List<?>) response.getBody()).size());
        verify(transactionRepo, times(1)).findAllByOrderByIdAsc();
    }

    @Test
    void testGetHistoryByNumberNotFound() {
        String cardNumber = "1234567890123456";
        when(transactionRepo.findAllByNumberOrderByIdAsc(cardNumber)).thenReturn(Arrays.asList());

        assertThrows(NotFoundException.class, () -> transactionService.getHistoryByNumber(cardNumber));
        verify(transactionRepo, times(1)).findAllByNumberOrderByIdAsc(cardNumber);
    }

    @Test
    void testGetHistoryByNumberSuccessfully() {
        String cardNumber = "1234567890123456";
        List<Transaction> transactions = Arrays.asList(new Transaction(), new Transaction());
        when(transactionRepo.findAllByNumberOrderByIdAsc(cardNumber)).thenReturn(transactions);

        ResponseEntity<List<FeedbackResponseDTO>> response = transactionService.getHistoryByNumber(cardNumber);

        assertNotNull(response);
        assertEquals(2, response.getBody().size());
        verify(transactionRepo, times(1)).findAllByNumberOrderByIdAsc(cardNumber);
    }

    @Test
    void testAddFeedbackNotFound() {
        when(transactionRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> transactionService.addFeedback(feedbackDTO));
        verify(transactionRepo, times(1)).findById(1L);
        verify(transactionRepo, times(0)).save(any(Transaction.class));
    }

    @Test
    void testUpdateLimitWhenAllowedFeedbackForManualProcessingTransaction() {
        transaction.setResult(TransactionType.MANUAL_PROCESSING.toString());
        transaction.setAmount(500L);

        long initialMaxAllowed = Constants.MAX_ALLOWED;

        ConstantsUtil.updateTransactionLimit(TransactionType.ALLOWED.toString(), transaction);

        long expectedMaxAllowed = (long) Math.ceil(0.8 * initialMaxAllowed + 0.2 * 500);
        assertEquals(expectedMaxAllowed, Constants.MAX_ALLOWED);
    }

    @Test
    void testUpdateLimitWhenAllowedFeedbackForProhibitedTransaction() {
        transaction.setResult(TransactionType.PROHIBITED.toString());
        transaction.setAmount(1000L);

        long initialMaxAllowed = Constants.MAX_ALLOWED;
        long initialMaxManualProcessing = Constants.MAX_MANUAL_PROCESSING;

        ConstantsUtil.updateTransactionLimit(TransactionType.ALLOWED.toString(), transaction);

        long expectedMaxAllowed = (long) Math.ceil(0.8 * initialMaxAllowed + 0.2 * 1000);
        long expectedMaxManualProcessing = (long) Math.ceil(0.8 * initialMaxManualProcessing + 0.2 * 1000);

        assertEquals(expectedMaxAllowed, Constants.MAX_ALLOWED);
        assertEquals(expectedMaxManualProcessing, Constants.MAX_MANUAL_PROCESSING);
    }

    @Test
    void testUpdateLimitWhenManualFeedbackForAllowedTransaction() {
        transaction.setResult(TransactionType.ALLOWED.toString());
        transaction.setAmount(300L);

        long initialMaxAllowed = Constants.MAX_ALLOWED;

        ConstantsUtil.updateTransactionLimit(TransactionType.MANUAL_PROCESSING.toString(), transaction);

        long expectedMaxAllowed = (long) Math.ceil(0.8 * initialMaxAllowed - 0.2 * 300);
        assertEquals(expectedMaxAllowed, Constants.MAX_ALLOWED);
    }

    @Test
    void testUpdateLimitWhenManualFeedbackForProhibitedTransaction() {
        transaction.setResult(TransactionType.PROHIBITED.toString());
        transaction.setAmount(2000L);

        long initialMaxManualProcessing = Constants.MAX_MANUAL_PROCESSING;

        ConstantsUtil.updateTransactionLimit(TransactionType.MANUAL_PROCESSING.toString(), transaction);

        long expectedMaxManualProcessing = (long) Math.ceil(0.8 * initialMaxManualProcessing + 0.2 * 2000);
        assertEquals(expectedMaxManualProcessing, Constants.MAX_MANUAL_PROCESSING);
    }

    @Test
    void testUpdateLimitWhenProhibitedFeedbackForAllowedTransaction() {
        transaction.setResult(TransactionType.ALLOWED.toString());
        transaction.setAmount(250L);

        long initialMaxAllowed = Constants.MAX_ALLOWED;
        long initialMaxManualProcessing = Constants.MAX_MANUAL_PROCESSING;

        ConstantsUtil.updateTransactionLimit(TransactionType.PROHIBITED.toString(), transaction);

        long expectedMaxAllowed = (long) Math.ceil(0.8 * initialMaxAllowed - 0.2 * 250);
        long expectedMaxManualProcessing = (long) Math.ceil(0.8 * initialMaxManualProcessing - 0.2 * 250);

        assertEquals(expectedMaxAllowed, Constants.MAX_ALLOWED);
        assertEquals(expectedMaxManualProcessing, Constants.MAX_MANUAL_PROCESSING);
    }

    @Test
    void testUpdateLimitWhenProhibitedFeedbackForManualProcessingTransaction() {
        transaction.setResult(TransactionType.MANUAL_PROCESSING.toString());
        transaction.setAmount(1500L); // Example amount for this test

        long initialMaxManualProcessing = Constants.MAX_MANUAL_PROCESSING;

        ConstantsUtil.updateTransactionLimit(TransactionType.PROHIBITED.toString(), transaction);

        long expectedMaxManualProcessing = (long) Math.ceil(0.8 * initialMaxManualProcessing - 0.2 * 1500);

        assertEquals(expectedMaxManualProcessing, Constants.MAX_MANUAL_PROCESSING);
    }

    @Test
    void testAddFeedbackUpdatesLimits() {
        transaction.setResult(TransactionType.MANUAL_PROCESSING.toString());
        transaction.setFeedback(null);
        when(transactionRepo.findById(1L)).thenReturn(Optional.of(transaction));

        FeedbackRequestDTO feedbackRequest = new FeedbackRequestDTO();
        feedbackRequest.setTransactionId(1L);
        feedbackRequest.setFeedback(TransactionType.ALLOWED.toString());

        ResponseEntity<FeedbackResponseDTO> response = transactionService.addFeedback(feedbackRequest);

        assertNotNull(response);
        assertEquals(TransactionType.ALLOWED.toString(), response.getBody().getFeedback());
        verify(transactionRepo, times(1)).save(transaction);
    }
}
