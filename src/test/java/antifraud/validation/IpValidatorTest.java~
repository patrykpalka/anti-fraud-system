package antifraud.validation;

import antifraud.validation.annotation.IpValidator;
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

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class IpValidatorTest {

    @Mock
    private ConstraintValidatorContext mockContext;

    @InjectMocks
    private IpValidator validator;

    @BeforeEach
    void setUp() {
        validator = new IpValidator();
    }

    @Test
    @DisplayName("Should return true for valid IPv4 addresses")
    void shouldReturnTrueForValidIpWithStandardIPv4() {
        assertTrue(validator.isValid("192.168.0.1", mockContext));
        assertTrue(validator.isValid("127.0.0.1", mockContext));
        assertTrue(validator.isValid("237.84.2.178", mockContext));
    }

    @Test
    @DisplayName("Should return false for IP with non-numeric characters")
    void shouldReturnFalseForInvalidIpWithNonNumericCharacters() {
        assertFalse(validator.isValid("192.168.0.a", mockContext));
    }

    @Test
    @DisplayName("Should return false for IP with spaces")
    void shouldReturnFalseForInvalidIpWithSpaces() {
        assertFalse(validator.isValid("192. 168.0.1", mockContext));
    }

    @Test
    @DisplayName("Should return false for IP that is too short")
    void shouldReturnFalseForInvalidIpTooShort() {
        assertFalse(validator.isValid("1.1.1", mockContext));
    }

    @Test
    @DisplayName("Should return false for IP with IPv6 format")
    void shouldReturnFalseForInvalidIpWithIPv6Format() {
        assertFalse(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:7334", mockContext));
    }

    @Test
    @DisplayName("Should return false for empty IP string")
    void shouldReturnFalseForEmptyIpString() {
        assertFalse(validator.isValid("", mockContext));
    }

    @Test
    @DisplayName("Should return false for overly long IP input")
    void shouldReturnFalseForLongIpInput() {
        assertFalse(validator.isValid("192.168.0.1.1.2.3.4.5.6.7.8.9.10.11.12", mockContext));
    }
}
