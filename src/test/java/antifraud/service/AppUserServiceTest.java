package antifraud.service;

import antifraud.dto.request.UserRegistrationRequestDTO;
import antifraud.enums.RoleNames;
import antifraud.model.AppUser;
import antifraud.model.Role;
import antifraud.repo.AppUserRepo;
import antifraud.repo.RoleRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTest {

    @Mock
    private AppUserRepo appUserRepo;

    @Mock
    private RoleRepo roleRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AppUserService appUserService;

    private Role merchantRole;
    private Role administratorRole;

    @BeforeEach
    void setUp() {
        merchantRole = new Role("ROLE_MERCHANT");
        administratorRole = new Role("ROLE_ADMINISTRATOR");
    }

    @Test
    void registerFirstUser_shouldAssignAdministratorRoleAndUnlockedStatus() {
        // Arrange
        UserRegistrationRequestDTO registration = createRegistrationDTO();

        // Mock behavior for first user
        when(appUserRepo.count()).thenReturn(0L);

        // Mock role repository
        when(roleRepo.findByName(RoleNames.ROLE_ADMINISTRATOR.toString()))
                .thenReturn(Optional.of(administratorRole));

        // Mock password encoding
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Mock save operation
        when(appUserRepo.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AppUser registeredUser = appUserService.register(registration);

        // Assert
        assertNotNull(registeredUser);
        assertFalse(registeredUser.isLocked());
        assertTrue(registeredUser.getRoles().contains(administratorRole));
        verify(appUserRepo).save(registeredUser);
        verify(roleRepo).findByName(RoleNames.ROLE_ADMINISTRATOR.toString());
    }

    @Test
    void registerSubsequentUser_shouldAssignMerchantRoleAndLockedStatus() {
        // Arrange
        UserRegistrationRequestDTO registration = createRegistrationDTO();

        // Mock behavior for subsequent users
        when(appUserRepo.count()).thenReturn(1L);

        // Mock role repository
        when(roleRepo.findByName(RoleNames.ROLE_MERCHANT.toString()))
                .thenReturn(Optional.of(merchantRole));

        // Mock password encoding
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Mock save operation
        when(appUserRepo.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AppUser registeredUser = appUserService.register(registration);

        // Assert
        assertNotNull(registeredUser);
        assertTrue(registeredUser.isLocked());
        assertTrue(registeredUser.getRoles().contains(merchantRole));
        verify(appUserRepo).save(registeredUser);
        verify(roleRepo).findByName(RoleNames.ROLE_MERCHANT.toString());
    }

    @Test
    void registerUser_shouldThrowExceptionWhenRoleNotFound() {
        // Arrange
        UserRegistrationRequestDTO registration = createRegistrationDTO();

        // Mock behavior for first user
        when(appUserRepo.count()).thenReturn(0L);

        // Simulate role not found
        when(roleRepo.findByName(RoleNames.ROLE_ADMINISTRATOR.toString()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            appUserService.register(registration);
        });
    }

    // Helper method to create a registration DTO for testing
    private UserRegistrationRequestDTO createRegistrationDTO() {
        UserRegistrationRequestDTO dto = new UserRegistrationRequestDTO();
        dto.setUsername("testuser");
        dto.setName("Test User");
        dto.setPassword("password123");
        return dto;
    }
}