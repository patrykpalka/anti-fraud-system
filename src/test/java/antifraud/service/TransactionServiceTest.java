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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepo transactionRepo;

    @Mock
    private SuspiciousIpRepo suspiciousIpRepo;

    @Mock
    private StolenCardRepo stolenCardRepo;

    @InjectMocks
    private TransactionService transactionService;

    private TransactionRequestDTO transactionDTO;
    private FeedbackRequestDTO feedbackDTO;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
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
    void shouldSuccessfullyAddTransaction() {
        when(transactionRepo.save(any(Transaction.class))).thenReturn(transaction);

        ResponseEntity<TransactionResponseDTO> response = transactionService.addTransaction(transactionDTO);

        assertNotNull(response, "Response should not be null.");
        assertEquals(TransactionType.ALLOWED.toString(), response.getBody().getResult(), "Transaction result should be ALLOWED.");
        verify(transactionRepo, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw NotFoundException when adding feedback to a non-existent transaction")
    void shouldThrowNotFoundExceptionWhenAddingFeedbackToNonExistentTransaction() {
        when(transactionRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> transactionService.addFeedback(feedbackDTO),
                "Expected NotFoundException when transaction is not found.");
        verify(transactionRepo, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ConflictException when feedback is already set on a transaction")
    void shouldThrowConflictExceptionWhenFeedbackAlreadySet() {
        transaction.setFeedback("APPROVED");
        when(transactionRepo.findById(1L)).thenReturn(Optional.of(transaction));

        assertThrows(ConflictException.class, () -> transactionService.addFeedback(feedbackDTO),
                "Expected ConflictException when feedback is already set on the transaction.");
        verify(transactionRepo, times(1)).findById(1L);
        verify(transactionRepo, times(0)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw UnprocessableEntityException when feedback and result are the same")
    void shouldThrowUnprocessableEntityExceptionWhenFeedbackAndResultAreTheSame() {
        transaction.setResult("APPROVED");
        when(transactionRepo.findById(1L)).thenReturn(Optional.of(transaction));

        UnprocessableEntityException exception = assertThrows(UnprocessableEntityException.class, () -> transactionService.addFeedback(feedbackDTO));
        assertEquals("Result and feedback cannot be the same", exception.getMessage(), "Expected exception message did not match.");
        verify(transactionRepo, times(1)).findById(1L);
        verify(transactionRepo, times(0)).save(any(Transaction.class));
    }

    @Test
    void shouldReturnTransactionHistory() {
        List<Transaction> transactions = Arrays.asList(new Transaction(), new Transaction());
        when(transactionRepo.findAllByOrderByIdAsc()).thenReturn(transactions);

        ResponseEntity<?> response = transactionService.getHistory();

        assertNotNull(response, "Response should not be null.");
        assertEquals(2, ((List<?>) response.getBody()).size(), "The transaction history size should match.");
        verify(transactionRepo, times(1)).findAllByOrderByIdAsc();
    }

    @Test
    @DisplayName("Should throw NotFoundException when no transactions are found for a card number")
    void shouldThrowNotFoundExceptionWhenNoTransactionsFoundForCardNumber() {
        String cardNumber = "1234567890123456";
        when(transactionRepo.findAllByNumberOrderByIdAsc(cardNumber)).thenReturn(Arrays.asList());

        assertThrows(NotFoundException.class, () -> transactionService.getHistoryByNumber(cardNumber),
                "Expected NotFoundException when no transactions are found for the given card number.");
        verify(transactionRepo, times(1)).findAllByNumberOrderByIdAsc(cardNumber);
    }

    @Test
    void shouldReturnTransactionHistoryForCardNumber() {
        String cardNumber = "1234567890123456";
        List<Transaction> transactions = Arrays.asList(new Transaction(), new Transaction());
        when(transactionRepo.findAllByNumberOrderByIdAsc(cardNumber)).thenReturn(transactions);

        ResponseEntity<List<FeedbackResponseDTO>> response = transactionService.getHistoryByNumber(cardNumber);

        assertNotNull(response, "Response should not be null.");
        assertEquals(2, response.getBody().size(), "The transaction history size should match.");
        verify(transactionRepo, times(1)).findAllByNumberOrderByIdAsc(cardNumber);
    }

    @Test
    @DisplayName("Should update limits correctly for an allowed feedback on a manual processing transaction")
    void shouldUpdateLimitsCorrectlyForAllowedFeedbackOnManualProcessing() {
        transaction.setResult(TransactionType.MANUAL_PROCESSING.toString());
        transaction.setAmount(500L);
        long initialMaxAllowed = Constants.MAX_ALLOWED;

        ConstantsUtil.updateTransactionLimit(TransactionType.ALLOWED.toString(), transaction);

        long expectedMaxAllowed = (long) Math.ceil(0.8 * initialMaxAllowed + 0.2 * 500);
        assertEquals(expectedMaxAllowed, Constants.MAX_ALLOWED, "MAX_ALLOWED should be updated correctly.");
    }

    @Test
    @DisplayName("Should update limits correctly for an allowed feedback on a prohibited transaction")
    void shouldUpdateLimitsCorrectlyForAllowedFeedbackOnProhibitedTransaction() {
        transaction.setResult(TransactionType.PROHIBITED.toString());
        transaction.setAmount(1000L);
        long initialMaxAllowed = Constants.MAX_ALLOWED;
        long initialMaxManualProcessing = Constants.MAX_MANUAL_PROCESSING;

        ConstantsUtil.updateTransactionLimit(TransactionType.ALLOWED.toString(), transaction);

        long expectedMaxAllowed = (long) Math.ceil(0.8 * initialMaxAllowed + 0.2 * 1000);
        long expectedMaxManualProcessing = (long) Math.ceil(0.8 * initialMaxManualProcessing + 0.2 * 1000);
        assertEquals(expectedMaxAllowed, Constants.MAX_ALLOWED, "MAX_ALLOWED should be updated correctly.");
        assertEquals(expectedMaxManualProcessing, Constants.MAX_MANUAL_PROCESSING, "MAX_MANUAL_PROCESSING should be updated correctly.");
    }

    @Test
    @DisplayName("Should update limits correctly for manual feedback on an allowed transaction")
    void shouldUpdateLimitsCorrectlyForManualFeedbackOnAllowedTransaction() {
        transaction.setResult(TransactionType.ALLOWED.toString());
        transaction.setAmount(300L);
        long initialMaxAllowed = Constants.MAX_ALLOWED;

        ConstantsUtil.updateTransactionLimit(TransactionType.MANUAL_PROCESSING.toString(), transaction);

        long expectedMaxAllowed = (long) Math.ceil(0.8 * initialMaxAllowed - 0.2 * 300);
        assertEquals(expectedMaxAllowed, Constants.MAX_ALLOWED, "MAX_ALLOWED should be updated correctly.");
    }

    @Test
    @DisplayName("Should update limits correctly for manual feedback on a prohibited transaction")
    void shouldUpdateLimitsCorrectlyForManualFeedbackOnProhibitedTransaction() {
        transaction.setResult(TransactionType.PROHIBITED.toString());
        transaction.setAmount(2000L);
        long initialMaxManualProcessing = Constants.MAX_MANUAL_PROCESSING;

        ConstantsUtil.updateTransactionLimit(TransactionType.MANUAL_PROCESSING.toString(), transaction);

        long expectedMaxManualProcessing = (long) Math.ceil(0.8 * initialMaxManualProcessing + 0.2 * 2000);
        assertEquals(expectedMaxManualProcessing, Constants.MAX_MANUAL_PROCESSING, "MAX_MANUAL_PROCESSING should be updated correctly.");
    }

    @Test
    @DisplayName("Should update limits correctly for prohibited feedback on an allowed transaction")
    void shouldUpdateLimitsCorrectlyForProhibitedFeedbackOnAllowedTransaction() {
        transaction.setResult(TransactionType.ALLOWED.toString());
        transaction.setAmount(250L);
        long initialMaxAllowed = Constants.MAX_ALLOWED;
        long initialMaxManualProcessing = Constants.MAX_MANUAL_PROCESSING;

        ConstantsUtil.updateTransactionLimit(TransactionType.PROHIBITED.toString(), transaction);

        long expectedMaxAllowed = (long) Math.ceil(0.8 * initialMaxAllowed - 0.2 * 250);
        long expectedMaxManualProcessing = (long) Math.ceil(0.8 * initialMaxManualProcessing - 0.2 * 250);
        assertEquals(expectedMaxAllowed, Constants.MAX_ALLOWED, "MAX_ALLOWED should be updated correctly.");
        assertEquals(expectedMaxManualProcessing, Constants.MAX_MANUAL_PROCESSING, "MAX_MANUAL_PROCESSING should be updated correctly.");
    }

    @Test
    @DisplayName("Should update limits correctly for prohibited feedback on a manual processing transaction")
    void shouldUpdateLimitsCorrectlyForProhibitedFeedbackOnManualProcessing() {
        transaction.setResult(TransactionType.MANUAL_PROCESSING.toString());
        transaction.setAmount(1500L);
        long initialMaxManualProcessing = Constants.MAX_MANUAL_PROCESSING;

        ConstantsUtil.updateTransactionLimit(TransactionType.PROHIBITED.toString(), transaction);

        long expectedMaxManualProcessing = (long) Math.ceil(0.8 * initialMaxManualProcessing - 0.2 * 1500);
        assertEquals(expectedMaxManualProcessing, Constants.MAX_MANUAL_PROCESSING, "MAX_MANUAL_PROCESSING should be updated correctly.");
    }

    @Test
    void shouldUpdateLimitsWhenAddingFeedback() {
        transaction.setResult(TransactionType.MANUAL_PROCESSING.toString());
        transaction.setFeedback(null);
        when(transactionRepo.findById(1L)).thenReturn(Optional.of(transaction));

        FeedbackRequestDTO feedbackRequest = new FeedbackRequestDTO();
        feedbackRequest.setTransactionId(1L);
        feedbackRequest.setFeedback(TransactionType.ALLOWED.toString());

        ResponseEntity<FeedbackResponseDTO> response = transactionService.addFeedback(feedbackRequest);

        assertNotNull(response, "Response should not be null.");
        assertEquals(TransactionType.ALLOWED.toString(), response.getBody().getFeedback(), "Feedback should be updated correctly.");
        verify(transactionRepo, times(1)).save(transaction);
    }
}
