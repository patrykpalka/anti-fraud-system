package antifraud;

import antifraud.dto.request.TransactionRequestDTO;
import antifraud.enums.TransactionType;
import antifraud.model.Transaction;
import antifraud.validation.transaction.AmountValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransactionRequestTest {

    @Autowired
    private ObjectMapper mapper;
    private AmountValidator amountValidator;

    @BeforeEach
    void setUp() {
        amountValidator = new AmountValidator();
    }

    @Test
    @DisplayName("Should correctly convert DTO to Transaction model")
    void shouldConvertDtoToTransaction() {
        TransactionRequestDTO dto = createTransactionRequestDTO();
        Transaction expectedTransaction = createExpectedTransaction();

        Transaction actualTransaction = dto.getTransaction();

        assertEquals(expectedTransaction, actualTransaction);
    }

    @Test
    @DisplayName("Should return ALLOWED for transactions with allowed amount")
    void shouldReturnAllowedForAllowedAmount() throws JsonProcessingException {
        String json = createTransactionJson(100);
        TransactionRequestDTO dto = mapper.readValue(json, TransactionRequestDTO.class);
        List<String> reasons = new ArrayList<>();

        TransactionType result = amountValidator.validate(dto, reasons, TransactionType.ALLOWED);

        assertEquals(TransactionType.ALLOWED, result);
        assertTrue(reasons.isEmpty());
    }

    @Test
    @DisplayName("Should return MANUAL_PROCESSING for transactions with high amount")
    void shouldReturnManualProcessingForHighAmount() throws JsonProcessingException {
        // Arrange
        String json = createTransactionJson(1000);
        TransactionRequestDTO dto = mapper.readValue(json, TransactionRequestDTO.class);
        List<String> reasons = new ArrayList<>();

        // Act
        TransactionType result = amountValidator.validate(dto, reasons, TransactionType.MANUAL_PROCESSING);

        // Assert
        assertEquals(TransactionType.MANUAL_PROCESSING, result);
        assertTrue(reasons.contains("amount"));
    }

    @Test
    @DisplayName("Should return PROHIBITED for transactions with very high amount")
    void shouldReturnProhibitedForVeryHighAmount() throws JsonProcessingException {
        // Arrange
        String json = createTransactionJson(2000);
        TransactionRequestDTO dto = mapper.readValue(json, TransactionRequestDTO.class);
        List<String> reasons = new ArrayList<>();

        // Act
        TransactionType result = amountValidator.validate(dto, reasons, TransactionType.PROHIBITED);

        // Assert
        assertEquals(TransactionType.PROHIBITED, result);
        assertTrue(reasons.contains("amount"));
    }

    @Test
    @DisplayName("Should throw JsonProcessingException for invalid amount format")
    void shouldThrowExceptionForInvalidAmountFormat() {
        // Arrange
        String json = createInvalidAmountJson();

        // Act & Assert
        assertThrows(JsonProcessingException.class, () -> {
            mapper.readValue(json, TransactionRequestDTO.class);
        });
    }

    @Test
    @DisplayName("Should throw JsonProcessingException for invalid date format")
    void shouldThrowExceptionForInvalidDateFormat() {
        // Arrange
        String json = createInvalidDateJson();

        // Act & Assert
        assertThrows(JsonProcessingException.class, () -> {
            mapper.readValue(json, TransactionRequestDTO.class);
        });
    }

    @Test
    @DisplayName("Should detect missing required fields during validation")
    void shouldDetectMissingFields() throws JsonProcessingException {
        // Arrange
        String json = createTransactionJson(100);
        TransactionRequestDTO dto = mapper.readValue(json, TransactionRequestDTO.class);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Act
        Set<ConstraintViolation<TransactionRequestDTO>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should detect validation errors for null fields")
    void shouldDetectValidationErrorsForNullFields() throws JsonProcessingException {
        // Arrange
        String json = """
        {
            "amount": 100,
            "ip": null,
            "number": "1234567890123456",
            "region": "EAP",
            "date": "2023-12-08T10:15:30"
        }
        """;
        TransactionRequestDTO dto = mapper.readValue(json, TransactionRequestDTO.class);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Act
        Set<ConstraintViolation<TransactionRequestDTO>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty());
    }

    // Helper methods for creating test data
    private TransactionRequestDTO createTransactionRequestDTO() {
        TransactionRequestDTO dto = new TransactionRequestDTO();
        dto.setAmount(100);
        dto.setIp("123.45.67.89");
        dto.setNumber("1234567890123456");
        dto.setRegion("EAP");
        dto.setDate(LocalDateTime.parse("2023-12-08T10:15:30"));
        return dto;
    }

    private Transaction createExpectedTransaction() {
        return new Transaction(100, "123.45.67.89", "1234567890123456", "EAP", LocalDateTime.parse("2023-12-08T10:15:30"));
    }

    private String createTransactionJson(int amount) {
        return String.format("""
        {
            "amount": %d,
            "ip": "123.45.67.89",
            "number": "1234567890123456",
            "region": "EAP",
            "date": "2023-12-08T10:15:30"
        }
        """, amount);
    }

    private String createInvalidAmountJson() {
        return """
        {
            "amount": abcdefghij,
            "ip": "123.45.67.89",
            "number": "1234567890123456",
            "region": "EAP",
            "date": "2023-12-08T10:15:30"
        }
        """;
    }

    private String createInvalidDateJson() {
        return """
        {
            "amount": 100,
            "ip": "123.45.67.89",
            "number": "1234567890123456",
            "region": "EAP",
            "date": "2023.12.08T10;15;30"
        }
        """;
    }
}
