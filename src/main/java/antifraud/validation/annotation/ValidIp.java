package antifraud.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for validating that an IP address is in a valid format (IPv4).
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IpValidator.class)
public @interface ValidIp {
    String message() default "Invalid IP";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
