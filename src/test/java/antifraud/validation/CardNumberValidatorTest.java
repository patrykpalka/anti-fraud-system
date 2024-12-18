package antifraud.validation;

import antifraud.validation.annotation.CardNumberValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class CardNumberValidatorTest {

    @Mock
    private ConstraintValidatorContext mockContext;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder mockViolationBuilder;

    @InjectMocks
    private CardNumberValidator validator;

    @BeforeEach
    void setUp() {
        when(mockContext.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(mockViolationBuilder);

        lenient().when(mockContext.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(mockViolationBuilder);
    }

    @Test
    void shouldReturnTrueForValidVisaCard() {
        // Given a valid Visa card number
        String validVisaCard = "4532015112830366";

        // When validating the card
        boolean isValid = validator.isValid(validVisaCard, mockContext);

        // Then the result should be true
        assertTrue(isValid);
        verifyNoViolation(mockContext);
    }

    @Test
    void shouldReturnTrueForValidMasterCard() {
        // Given a valid MasterCard number
        String validMasterCard = "5425233430109903";

        // When validating the card
        boolean isValid = validator.isValid(validMasterCard, mockContext);

        // Then the result should be true
        assertTrue(isValid);
        verifyNoViolation(mockContext);
    }

    @Test
    void shouldReturnFalseForCardTooShort() {
        // Given a card number that is too short
        String shortCard = "123456789012345";

        // When validating the card
        boolean isValid = validator.isValid(shortCard, mockContext);

        // Then the result should be false and a violation should be triggered
        assertFalse(isValid);
        verifyViolation(mockContext, "Card number must be 16 digits");
    }

    @Test
    void shouldReturnFalseForNonNumericCard() {
        // Given a card number containing non-numeric characters
        String nonNumericCard = "4532abcd11283036";

        // When validating the card
        boolean isValid = validator.isValid(nonNumericCard, mockContext);

        // Then the result should be false and a violation should be triggered
        assertFalse(isValid);
        verifyViolation(mockContext, "Card number must contain only digits");
    }

    @Test
    void shouldReturnFalseForInvalidCardAccordingToLuhnAlgorithm() {
        // Given a card number that fails the Luhn algorithm check
        String invalidCard = "4532015112830367";

        // When validating the card
        boolean isValid = validator.isValid(invalidCard, mockContext);

        // Then the result should be false and a violation should be triggered
        assertFalse(isValid);
        verifyViolation(mockContext, "Invalid card number according to the Luhn algorithm");
    }

    @Test
    void shouldReturnFalseForSingleDigitCard() {
        // Given a single-digit card number
        String singleDigitCard = "1";

        // When validating the card
        boolean isValid = validator.isValid(singleDigitCard, mockContext);

        // Then the result should be false and no violation is verified
        assertFalse(isValid);
        verifyViolation(mockContext, "Card number must be 16 digits");
    }

    private void verifyViolation(ConstraintValidatorContext context, String expectedMessage) {
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate(expectedMessage);
        verify(mockViolationBuilder).addConstraintViolation();
    }

    private void verifyNoViolation(ConstraintValidatorContext context) {
        verify(context, never()).disableDefaultConstraintViolation();
        verify(context, never()).buildConstraintViolationWithTemplate(anyString());
        verify(mockViolationBuilder, never()).addConstraintViolation();
    }
}
