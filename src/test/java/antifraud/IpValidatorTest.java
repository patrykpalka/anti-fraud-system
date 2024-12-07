package antifraud;

import antifraud.validation.annotation.IpValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class IpValidatorTest {

    private IpValidator validator;

    @Mock
    private ConstraintValidatorContext mockContext;

    @BeforeEach
    void setUp() {
        validator = new IpValidator();
    }

    @Test
    void testValidIpWithStandardIPv4() {
        assertTrue(validator.isValid("192.168.0.1", mockContext));
        assertTrue(validator.isValid("127.0.0.1", mockContext));
        assertTrue(validator.isValid("237.84.2.178", mockContext));
    }

    @Test
    void testInvalidIpWithNonNumericCharacters() {
        assertFalse(validator.isValid("192.168.0.a", mockContext));
    }

    @Test
    void testInvalidIpWithSpaces() {
        assertFalse(validator.isValid("192. 168.0.1", mockContext));
    }

    @Test
    void testInvalidIpTooShort() {
        assertFalse(validator.isValid("1.1.1", mockContext));
    }

    @Test
    void testInvalidIpWithIPv6Format() {
        assertFalse(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:7334", mockContext));
    }

    @Test
    void testEmptyIpString() {
        assertFalse(validator.isValid("", mockContext));
    }

    @Test
    void testLongIpInput() {
        assertFalse(validator.isValid("192.168.0.1.1.2.3.4.5.6.7.8.9.10.11.12", mockContext));
    }
}
