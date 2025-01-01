package antifraud.config;

import antifraud.exception.NotFoundException;
import antifraud.logging.events.authentication.BruteForceAttemptEvent;
import antifraud.logging.events.authentication.FailedLoginEvent;
import antifraud.logging.events.user.UserLockedStatusChangeEvent;
import antifraud.model.AppUser;
import antifraud.model.FailedLoginAttempt;
import antifraud.repo.AppUserRepo;
import antifraud.repo.FailedLoginAttemptRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final FailedLoginAttemptRepo failedLoginAttemptRepo;
    private final ApplicationEventPublisher eventPublisher;
    private final AppUserRepo appUserRepo;

    @Value("${security.failed-login-threshold:5}") // Default to 5 if not specified
    private int failedLoginThreshold;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        // Save the failed login attempt
        String username = request.getParameter("username");
        AppUser user = appUserRepo.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
        failedLoginAttemptRepo.save(new FailedLoginAttempt(username));

        // Log failed login
        String ipAddress = request.getRemoteAddr();
        eventPublisher.publishEvent(new FailedLoginEvent(username, ipAddress, LocalDateTime.now()));

        int failedAttemptsCount = failedLoginAttemptRepo.countByUsername(username);

        // If failed attempts exceed threshold, block user
        if (failedAttemptsCount >= failedLoginThreshold) {
            user.setLocked(true);
            appUserRepo.save(user);

            eventPublisher.publishEvent(new BruteForceAttemptEvent(username, ipAddress, LocalDateTime.now()));
            eventPublisher.publishEvent(new UserLockedStatusChangeEvent(username, true));

            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User blocked due to too many failed login attempts");
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials");
        }
    }
}
