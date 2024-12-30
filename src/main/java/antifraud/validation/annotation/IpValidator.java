package antifraud.validation.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Validator for the {@link ValidIp} annotation.
 * Validates that the provided IP address is in a valid format (IPv4).
 */
public class IpValidator implements ConstraintValidator<ValidIp, String> {

    @Override
    public void initialize(ValidIp constraintAnnotation) {
        // No initialization needed for this validator.
    }

    @Override
    public boolean isValid(String ip, ConstraintValidatorContext context) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            return inetAddress.getHostAddress().equals(ip);
        } catch (UnknownHostException e) {
            return false;
        }
    }
}
