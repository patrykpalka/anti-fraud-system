package antifraud.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import antifraud.enums.TransactionType;

/**
 * Annotation for validating that a feedback value is valid based on the predefined
 * set of acceptable values defined in the {@link TransactionType} enum.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FeedbackValidator.class)
public @interface ValidFeedback {
    String message() default "Invalid feedback value";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
