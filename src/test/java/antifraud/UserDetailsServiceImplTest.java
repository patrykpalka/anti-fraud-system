package antifraud;

import antifraud.config.UserDetailsServiceImpl;
import antifraud.model.AppUser;
import antifraud.repo.AppUserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @Mock
    private AppUserRepo appUserRepo;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsServiceImpl;

    private AppUser testUser;

    @BeforeEach
    void setUp() {
        testUser = new AppUser("Test User", "testuser", "encodedPassword");
    }

    @Test
    void shouldLoadUserByUsernameSuccessfully() {
        when(appUserRepo.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername("testuser");

        assertNotNull(userDetails, "UserDetails should not be null.");
        assertEquals("testuser", userDetails.getUsername(), "Username should match the input.");
        verify(appUserRepo, times(1)).findByUsername("testuser");
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionWhenUserNotFound() {
        when(appUserRepo.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                        userDetailsServiceImpl.loadUserByUsername("nonexistentuser"),
                "Expected UsernameNotFoundException when the user is not found.");
        verify(appUserRepo, times(1)).findByUsername("nonexistentuser");
    }
}
