package antifraud.config;

import antifraud.exception.NotFoundException;
import antifraud.logging.events.authentication.SuccessfulLoginEvent;
import antifraud.repo.AppUserRepo;
import antifraud.repo.FailedLoginAttemptRepo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AppUserRepo appUserRepo;
    private final FailedLoginAttemptRepo failedLoginAttemptRepo;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // Reset failed attempts counter
        String username = authentication.getName();
        appUserRepo.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
        failedLoginAttemptRepo.deleteByUsername(username);

        // Log successful login
        String ipAddress = request.getRemoteAddr();
        eventPublisher.publishEvent(new SuccessfulLoginEvent(username, ipAddress, LocalDateTime.now()));
    }
}
