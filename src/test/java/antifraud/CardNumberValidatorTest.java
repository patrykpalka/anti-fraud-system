package antifraud;

import antifraud.validation.annotation.CardNumberValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CardNumberValidatorTest {

    private CardNumberValidator validator;

    @Mock
    private ConstraintValidatorContext mockContext;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder mockViolationBuilder;

    @BeforeEach
    void setUp() {
        validator = new CardNumberValidator();
        when(mockContext.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(mockViolationBuilder);
        when(mockViolationBuilder.addConstraintViolation())
                .thenReturn(mockContext);
    }

    @Test
    void testValidCard() {
        assertTrue(validator.isValid("4532015112830366", mockContext)); // Valid Visa
        assertTrue(validator.isValid("5425233430109903", mockContext)); // Valid Mastercard
        verifyNoViolation(mockContext);
    }

    @Test
    void testInvalidCardTooShort() {
        assertFalse(validator.isValid("123456789012345", mockContext));
        verifyViolation(mockContext, "Card number must be 16 digits");
    }

    @Test
    void testInvalidCardNonNumeric() {
        assertFalse(validator.isValid("4532abcd11283036", mockContext));
        verifyViolation(mockContext, "Card number must contain only digits");
    }

    @Test
    void testInvalidCardLuhn() {
        assertFalse(validator.isValid("4532015112830367", mockContext));
        verifyViolation(mockContext, "Invalid card number according to the Luhn algorithm");
    }

    @Test
    void testEdgeCaseWithOneDigit() {
        assertFalse(validator.isValid("1", mockContext));
    }

    private void verifyViolation(ConstraintValidatorContext context, String expectedMessage) {
        verify(context, times(1)).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate(expectedMessage);
        verify(mockViolationBuilder).addConstraintViolation();
    }

    private void verifyNoViolation(ConstraintValidatorContext context) {
        verify(context, never()).disableDefaultConstraintViolation();
    }
}