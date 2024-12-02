package antifraud.validation.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for the {@link ValidCardNumber} annotation.
 * Validates that the card number meets the Luhn algorithm criteria,
 * is numeric, and has a length of 16 digits.
 */
public class CardNumberValidator implements ConstraintValidator<ValidCardNumber, String> {
    @Override
    public void initialize(ValidCardNumber constraintAnnotation) {
        // No initialization needed for this validator.
    }

    @Override
    public boolean isValid(String cardNumber, ConstraintValidatorContext context) {
        if (!isValidLength(cardNumber)) {
            buildConstraintViolation(context, "Card number must be 16 digits");
            return false;
        }

        if (!isNumeric(cardNumber)) {
            buildConstraintViolation(context, "Card number must contain only digits");
            return false;
        }

        if (!isValidLuhn(cardNumber)) {
            buildConstraintViolation(context, "Invalid card number according to the Luhn algorithm");
            return false;
        }

        return true;
    }

    private boolean isValidLength(String cardNumber) {
        return cardNumber.length() == 16;
    }

    private boolean isNumeric(String cardNumber) {
        return cardNumber.chars().allMatch(Character::isDigit);
    }

    private boolean isValidLuhn(String cardNumber) {
        int sum = 0;
        boolean alternate = false;

        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));
            if (alternate) {
                digit *= 2;
                if (digit > 9) digit -= 9;
            }
            sum += digit;
            alternate = !alternate;
        }

        return sum % 10 == 0;
    }

    private void buildConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
