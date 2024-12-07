package antifraud;

import antifraud.enums.RegionNames;
import antifraud.validation.annotation.RegionValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegionValidatorTest {

    private RegionValidator validator;

    @Mock
    private ConstraintValidatorContext mockContext;

    @BeforeEach
    void setUp() {
        validator = new RegionValidator();
    }

    @Test
    void testValidRegion() {
        assertTrue(validator.isValid("EAP", mockContext));
        assertTrue(validator.isValid("MENA", mockContext));
    }

    @Test
    void testAllEnumValues() {
        for (RegionNames region : RegionNames.values()) {
            assertTrue(validator.isValid(region.name(), mockContext));
        }
    }

    @Test
    void testCaseSensitivity() {
        assertFalse(validator.isValid("eap", mockContext));
        assertFalse(validator.isValid("Mena", mockContext));
        assertTrue(validator.isValid("EAP", mockContext));
    }

    @Test
    void testInvalidRegion() {
        assertFalse(validator.isValid("INVALID_REGION", mockContext));
    }

    @Test
    void testRegionWithLeadingOrTrailingWhitespace() {
        assertFalse(validator.isValid(" EAP", mockContext)); // Leading whitespace
        assertFalse(validator.isValid("EAP ", mockContext)); // Trailing whitespace
        assertFalse(validator.isValid(" EAP ", mockContext)); // Both leading and trailing whitespace
    }

    @Test
    void testRegionWithSpecialCharacters() {
        assertFalse(validator.isValid("E@P", mockContext)); // Special character should make it invalid
        assertFalse(validator.isValid("MENA!", mockContext)); // Special character should make it invalid
    }

    @Test
    void testNullRegion() {
        assertFalse(validator.isValid(null, mockContext));
    }

    @Test
    void testEmptyRegion() {
        assertFalse(validator.isValid("", mockContext));
    }
}
