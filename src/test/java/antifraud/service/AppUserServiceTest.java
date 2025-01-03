package antifraud.service;

import antifraud.dto.request.UserRegistrationRequestDTO;
import antifraud.enums.RoleNames;
import antifraud.exception.NotFoundException;
import antifraud.model.AppUser;
import antifraud.model.Role;
import antifraud.repo.AppUserRepo;
import antifraud.repo.RoleRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    private UserRegistrationRequestDTO registrationDTO;

    @BeforeEach
    void setUp() {
        merchantRole = new Role("ROLE_MERCHANT");
        administratorRole = new Role("ROLE_ADMINISTRATOR");
        registrationDTO = createRegistrationDTO();
    }

    @Test
    @DisplayName("Should assign administrator role and unlock status to the first registered user")
    void shouldAssignAdministratorRoleAndUnlockedStatusToFirstUser() {
        when(appUserRepo.count()).thenReturn(0L);
        when(roleRepo.findByName(RoleNames.ROLE_ADMINISTRATOR.toString()))
                .thenReturn(Optional.of(administratorRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(appUserRepo.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppUser registeredUser = appUserService.register(registrationDTO);

        assertNotNull(registeredUser, "The registered user should not be null.");
        assertFalse(registeredUser.isLocked(), "The first user should have an unlocked status.");
        assertTrue(registeredUser.getRoles().contains(administratorRole), "The first user should have the administrator role.");
        verify(appUserRepo).save(registeredUser);
        verify(roleRepo).findByName(RoleNames.ROLE_ADMINISTRATOR.toString());
    }

    @Test
    @DisplayName("Should assign merchant role and lock status to subsequent registered users")
    void shouldAssignMerchantRoleAndLockedStatusToSubsequentUsers() {
        when(appUserRepo.count()).thenReturn(1L);
        when(roleRepo.findByName(RoleNames.ROLE_MERCHANT.toString()))
                .thenReturn(Optional.of(merchantRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(appUserRepo.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppUser registeredUser = appUserService.register(registrationDTO);

        assertNotNull(registeredUser, "The registered user should not be null.");
        assertTrue(registeredUser.isLocked(), "Subsequent users should have a locked status.");
        assertTrue(registeredUser.getRoles().contains(merchantRole), "Subsequent users should have the merchant role.");
        verify(appUserRepo).save(registeredUser);
        verify(roleRepo).findByName(RoleNames.ROLE_MERCHANT.toString());
    }

    @Test
    void shouldThrowExceptionWhenRoleNotFound() {
        when(appUserRepo.count()).thenReturn(0L);

        when(roleRepo.findByName(RoleNames.ROLE_ADMINISTRATOR.toString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                appUserService.register(registrationDTO)
        );
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
