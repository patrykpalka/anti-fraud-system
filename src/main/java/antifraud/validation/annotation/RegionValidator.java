package antifraud.validation.annotation;

import antifraud.enums.RegionNames;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

/**
 * Validator for the {@link ValidRegion} annotation.
 * Validates that the provided region value is a valid entry in the {@link RegionNames} enum.
 */
public class RegionValidator implements ConstraintValidator<ValidRegion, String> {
    @Override
    public void initialize(ValidRegion constraintAnnotation) {
        // No initialization needed for this validator.
    }

    @Override
    public boolean isValid(String region, ConstraintValidatorContext context) {
        return Arrays.stream(RegionNames.values()).anyMatch(r -> r.name().equals(region));
    }
}
