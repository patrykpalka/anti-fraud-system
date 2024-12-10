package antifraud.validation;

import antifraud.enums.RegionNames;
import antifraud.validation.annotation.RegionValidator;
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
class RegionValidatorTest {

    @Mock
    private ConstraintValidatorContext mockContext;

    @InjectMocks
    private RegionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new RegionValidator();
    }

    @Test
    @DisplayName("Should return true for valid region names")
    void shouldReturnTrueForValidRegion() {
        assertTrue(validator.isValid("EAP", mockContext));
        assertTrue(validator.isValid("MENA", mockContext));
    }

    @Test
    @DisplayName("Should return true for all valid enum region values")
    void shouldReturnTrueForAllEnumValues() {
        for (RegionNames region : RegionNames.values()) {
            assertTrue(validator.isValid(region.name(), mockContext));
        }
    }

    @Test
    @DisplayName("Should return false for case-sensitive region names")
    void shouldReturnFalseForCaseSensitivity() {
        assertFalse(validator.isValid("eap", mockContext));
        assertFalse(validator.isValid("Mena", mockContext));
        assertTrue(validator.isValid("EAP", mockContext)); // Valid case should still return true
    }

    @Test
    void shouldReturnFalseForInvalidRegion() {
        assertFalse(validator.isValid("INVALID_REGION", mockContext));
    }

    @Test
    @DisplayName("Should return false for regions with leading or trailing whitespace")
    void shouldReturnFalseForRegionWithWhitespace() {
        assertFalse(validator.isValid(" EAP", mockContext)); // Leading whitespace
        assertFalse(validator.isValid("EAP ", mockContext)); // Trailing whitespace
        assertFalse(validator.isValid(" EAP ", mockContext)); // Both leading and trailing whitespace
    }

    @Test
    @DisplayName("Should return false for regions with special characters")
    void shouldReturnFalseForRegionWithSpecialCharacters() {
        // Act & Assert
        assertFalse(validator.isValid("E@P", mockContext)); // Special character should make it invalid
        assertFalse(validator.isValid("MENA!", mockContext)); // Special character should make it invalid
    }

    @Test
    void shouldReturnFalseForNullRegion() {
        assertFalse(validator.isValid(null, mockContext));
    }

    @Test
    void shouldReturnFalseForEmptyRegion() {
        assertFalse(validator.isValid("", mockContext));
    }
}
