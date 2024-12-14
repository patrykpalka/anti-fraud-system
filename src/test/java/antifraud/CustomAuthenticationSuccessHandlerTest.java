package antifraud;

import antifraud.config.CustomAuthenticationSuccessHandler;
import antifraud.logging.events.authentication.SuccessfulLoginEvent;
import antifraud.model.AppUser;
import antifraud.repo.AppUserRepo;
import antifraud.repo.FailedLoginAttemptRepo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomAuthenticationSuccessHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    private AppUserRepo appUserRepo;

    @Mock
    private FailedLoginAttemptRepo failedLoginAttemptRepo;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CustomAuthenticationSuccessHandler handler;

    @Test
    public void testOnAuthenticationSuccess() throws IOException, ServletException {
        // Arrange
        when(authentication.getName()).thenReturn("testUser");
        when(appUserRepo.findByUsername("testUser")).thenReturn(Optional.of(new AppUser()));

        // Act
        handler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        verify(failedLoginAttemptRepo).deleteByUsername("testUser");
        verify(eventPublisher).publishEvent(any(SuccessfulLoginEvent.class));
    }
}
