package antifraud;

import antifraud.validation.annotation.FeedbackValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class FeedbackValidatorTest {

    private final FeedbackValidator feedbackValidator = new FeedbackValidator();
    private final ConstraintValidatorContext mockContext = mock(ConstraintValidatorContext.class);

    @Test
    void testValidEnumValue() {
        assertTrue(feedbackValidator.isValid("ALLOWED", mockContext));
        assertTrue(feedbackValidator.isValid("MANUAL_PROCESSING", mockContext));
        assertTrue(feedbackValidator.isValid("PROHIBITED", mockContext));
    }

    @Test
    void testInvalidEnumValue() {
        assertFalse(feedbackValidator.isValid("INVALID_VALUE", mockContext));
        assertFalse(feedbackValidator.isValid("PROHIBITING", mockContext));
    }

    @Test
    void testNullValue() {
        assertFalse(feedbackValidator.isValid(null, mockContext));
    }
}
