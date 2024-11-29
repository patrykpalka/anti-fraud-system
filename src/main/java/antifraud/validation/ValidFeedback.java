package antifraud.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FeedbackValidator.class)
public @interface ValidFeedback {
    String message() default "Invalid feedback value";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
