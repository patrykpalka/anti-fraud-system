package antifraud;

import antifraud.config.CustomAuthenticationFailureHandler;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.util.ReflectionTestUtils;


import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationFailureHandlerTest {

    @InjectMocks
    private CustomAuthenticationFailureHandler handler;

    @Mock
    private FailedLoginAttemptRepo failedLoginAttemptRepo;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private AppUserRepo appUserRepo;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException exception;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(handler, "failedLoginThreshold", 5);
    }

    @Test
    public void testOnAuthenticationFailure() throws IOException {
        // Arrange
        when(request.getParameter("username")).thenReturn("testUser");
        AppUser user = new AppUser();
        when(appUserRepo.findByUsername("testUser")).thenReturn(Optional.of(user));

        // Act
        handler.onAuthenticationFailure(request, response, exception);

        // Assert
        verify(failedLoginAttemptRepo).save(any(FailedLoginAttempt.class));
        verify(eventPublisher).publishEvent(any(FailedLoginEvent.class));
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials");
    }

    @Test
    public void testOnAuthenticationFailureExceedThreshold() throws IOException {
        // Arrange
        when(request.getParameter("username")).thenReturn("testUser");
        AppUser user = new AppUser();
        when(appUserRepo.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(failedLoginAttemptRepo.countByUsername("testUser")).thenReturn(5);

        // Act
        handler.onAuthenticationFailure(request, response, exception);

        // Assert
        verify(failedLoginAttemptRepo).save(any(FailedLoginAttempt.class));
        verify(eventPublisher).publishEvent(any(FailedLoginEvent.class));
        verify(eventPublisher).publishEvent(any(BruteForceAttemptEvent.class));
        verify(eventPublisher).publishEvent(any(UserLockedStatusChangeEvent.class));
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "User blocked due to too many failed login attempts");
    }

    @Test
    public void testOnAuthenticationFailureUserNotFound() throws IOException {
        // Arrange
        when(request.getParameter("username")).thenReturn("testUser");
        when(appUserRepo.findByUsername("testUser")).thenReturn(Optional.empty());

        // Act and Assert
        try {
            handler.onAuthenticationFailure(request, response, exception);
        } catch (NotFoundException e) {
            verifyNoInteractions(failedLoginAttemptRepo);
            verifyNoInteractions(eventPublisher);
            verifyNoInteractions(response);
        }
    }
}
