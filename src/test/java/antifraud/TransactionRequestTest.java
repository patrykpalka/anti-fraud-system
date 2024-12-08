package antifraud;

import antifraud.dto.request.TransactionRequestDTO;
import antifraud.enums.TransactionType;
import antifraud.validation.transaction.AmountValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TransactionRequestTest {

    @Autowired
    private ObjectMapper mapper;
    private AmountValidator validator;

    @BeforeEach
    void setUp() {
        validator = new AmountValidator();
    }

    @Test
    void testAllowedAmount() throws JsonProcessingException {
        String json = """
        {
            "amount": 100,
            "ip": "123.45.67.89",
            "number": "1234567890123456",
            "region": "EAP",
            "date": "2023-12-08T10:15:30"
        }
        """;

        TransactionRequestDTO dto = mapper.readValue(json, TransactionRequestDTO.class);
        List<String> reasons = new ArrayList<>();
        TransactionType result = validator.validate(dto, reasons, TransactionType.ALLOWED);

        assertEquals(TransactionType.ALLOWED, result);
        assertTrue(reasons.isEmpty());
    }

    @Test
    void testManualProcessingAmount() throws JsonProcessingException {
        String json = """
        {
            "amount": 1000,
            "ip": "123.45.67.89",
            "number": "1234567890123456",
            "region": "EAP",
            "date": "2023-12-08T10:15:30"
        }
        """;

        TransactionRequestDTO dto = mapper.readValue(json, TransactionRequestDTO.class);
        List<String> reasons = new ArrayList<>();
        TransactionType result = validator.validate(dto, reasons, TransactionType.MANUAL_PROCESSING);

        assertEquals(TransactionType.MANUAL_PROCESSING, result);
        assertTrue(reasons.contains("amount"));
    }

    @Test
    void testProhibitedAmount() throws JsonProcessingException {
        String json = """
        {
            "amount": 2000,
            "ip": "123.45.67.89",
            "number": "1234567890123456",
            "region": "EAP",
            "date": "2023-12-08T10:15:30"
        }
        """;

        TransactionRequestDTO dto = mapper.readValue(json, TransactionRequestDTO.class);
        List<String> reasons = new ArrayList<>();
        TransactionType result = validator.validate(dto, reasons, TransactionType.PROHIBITED);

        assertEquals(TransactionType.PROHIBITED, result);
        assertTrue(reasons.contains("amount"));
    }

    @Test
    void testWrongAmountFormat() {
        String json = """
        {
            "amount": abcdefghij,
            "ip": "123.45.67.89",
            "number": "1234567890123456",
            "region": "EAP",
            "date": "2023-12-08T10:15:30"
        }
        """;

        Assertions.assertThrows(JsonProcessingException.class, () -> {
            mapper.readValue(json, TransactionRequestDTO.class);
        });
    }

    @Test
    void testWrongDateFormat() {
        String json = """
        {
            "amount": 100,
            "ip": "123.45.67.89",
            "number": "1234567890123456",
            "region": "EAP",
            "date": "2023.12.08T10;15;30"
        }
        """;

        Assertions.assertThrows(JsonProcessingException.class, () -> {
            mapper.readValue(json, TransactionRequestDTO.class);
        });
    }

    @Test
    void testMissingField() throws JsonProcessingException {
        String json = """
        {
            "amount": 100,
            "number": "1234567890123456",
            "region": "EAP",
            "date": "2023-12-08T10:15:30"
        }
        """;

        TransactionRequestDTO dto = mapper.readValue(json, TransactionRequestDTO.class);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<TransactionRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void testValidationAnnotations() throws JsonProcessingException {
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
        Set<ConstraintViolation<TransactionRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }
}