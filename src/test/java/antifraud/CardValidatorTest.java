package antifraud;

import antifraud.service.utils.ValidationUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CardValidatorTest {

    @Test
    void testValidCard() {
        assertTrue(ValidationUtil.isValidCardNumber("4532015112830366")); // Valid Visa
        assertTrue(ValidationUtil.isValidCardNumber("5425233430109903")); // Valid Mastercard
    }

    @Test
    void testInvalidCard() {
        assertFalse(ValidationUtil.isValidCardNumber("1234567890123456")); // Invalid number
        assertFalse(ValidationUtil.isValidCardNumber("4532015112830367")); // Visa with typo in last digit
    }

    @Test
    void testNullOrEmptyInput() {
        assertFalse(ValidationUtil.isValidCardNumber(null));
        assertFalse(ValidationUtil.isValidCardNumber(""));
    }

    @Test
    void testNonNumericInput() {
        assertFalse(ValidationUtil.isValidCardNumber("4532abcd11283036"));
        assertFalse(ValidationUtil.isValidCardNumber("4532 0151 1283 036"));
    }

    @Test
    void testEdgeCaseWithOneDigit() {
        assertFalse(ValidationUtil.isValidCardNumber("1"));
    }
}
