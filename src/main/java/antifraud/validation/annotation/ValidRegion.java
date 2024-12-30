package antifraud.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import antifraud.enums.RegionNames;

/**
 * Annotation for validating that a region is one of the predefined values defined in
 * the {@link RegionNames} enum.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RegionValidator.class)
public @interface ValidRegion {

    String message() default "Invalid region";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
