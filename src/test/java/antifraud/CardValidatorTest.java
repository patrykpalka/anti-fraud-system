package antifraud;

import antifraud.service.utils.VerificationUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CardValidatorTest {

    @Test
    void testValidCard() {
        assertTrue(VerificationUtil.isCardNumberValid("4532015112830366")); // Valid Visa
        assertTrue(VerificationUtil.isCardNumberValid("5425233430109903")); // Valid Mastercard
    }

    @Test
    void testInvalidCard() {
        assertFalse(VerificationUtil.isCardNumberValid("1234567890123456")); // Invalid number
        assertFalse(VerificationUtil.isCardNumberValid("4532015112830367")); // Visa with typo in last digit
    }

    @Test
    void testNullOrEmptyInput() {
        assertFalse(VerificationUtil.isCardNumberValid(null));
        assertFalse(VerificationUtil.isCardNumberValid(""));
    }

    @Test
    void testNonNumericInput() {
        assertFalse(VerificationUtil.isCardNumberValid("4532abcd11283036"));
        assertFalse(VerificationUtil.isCardNumberValid("4532 0151 1283 036"));
    }

    @Test
    void testEdgeCaseWithOneDigit() {
        assertFalse(VerificationUtil.isCardNumberValid("1"));
    }
}
