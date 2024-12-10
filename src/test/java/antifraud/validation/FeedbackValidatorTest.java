package antifraud.validation;

import antifraud.validation.annotation.FeedbackValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class FeedbackValidatorTest {

    @Mock
    private ConstraintValidatorContext mockContext;

    @InjectMocks
    private FeedbackValidator feedbackValidator;

    @BeforeEach
    void setUp() {
        feedbackValidator = new FeedbackValidator();
    }

    @Test
    void shouldReturnTrueForValidEnumValues() {
        assertTrue(feedbackValidator.isValid("ALLOWED", mockContext));
        assertTrue(feedbackValidator.isValid("MANUAL_PROCESSING", mockContext));
        assertTrue(feedbackValidator.isValid("PROHIBITED", mockContext));
    }

    @Test
    void shouldReturnFalseForInvalidEnumValues() {
        assertFalse(feedbackValidator.isValid("INVALID_VALUE", mockContext));
        assertFalse(feedbackValidator.isValid("PROHIBITING", mockContext));
    }

    @Test
    void shouldReturnFalseForNullValue() {
        assertFalse(feedbackValidator.isValid(null, mockContext));
    }
}
