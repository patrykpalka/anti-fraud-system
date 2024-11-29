package antifraud.validation;

import antifraud.enums.TransactionType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FeedbackValidator implements ConstraintValidator<ValidFeedback, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false; // Null values are invalid
        }
        try {
            // Check if the value exists in the FeedbackType enum
            TransactionType.valueOf(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false; // Invalid enum value
        }
    }
}
