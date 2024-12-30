package antifraud.validation.annotation;

import antifraud.enums.TransactionType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for the {@link ValidFeedback} annotation.
 * Validates that the provided feedback value is a valid enum value from {@link TransactionType}.
 */
public class FeedbackValidator implements ConstraintValidator<ValidFeedback, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false; // Null values are invalid
        }
        try {
            // Check if the value exists in the TransactionType enum
            TransactionType.valueOf(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false; // Invalid enum value
        }
    }
}
