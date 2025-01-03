package antifraud.service;

import antifraud.dto.request.UserRegistrationRequestDTO;
import antifraud.dto.request.UserRoleRequestDTO;
import antifraud.dto.request.UserStatusRequestDTO;
import antifraud.dto.response.OperationResponseDTO;
import antifraud.dto.response.UserDeletionResponseDTO;
import antifraud.dto.response.UserResponseDTO;
import antifraud.exception.BadRequestException;
import antifraud.exception.ConflictException;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private AppUserRepo appUserRepo;

    @Mock
    private RoleRepo roleRepo;

    @Mock
    private AppUserService appUserService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserService userService;

    private UserRegistrationRequestDTO registrationDTO;
    private UserRoleRequestDTO roleRequestDTO;
    private UserStatusRequestDTO statusRequestDTO;
    private AppUser user;

    @BeforeEach
    void setUp() {
        initTestData();
    }

    private void initTestData() {
        registrationDTO = new UserRegistrationRequestDTO();
        registrationDTO.setUsername("testuser");
        registrationDTO.setName("Test User");
        registrationDTO.setPassword("password");

        user = new AppUser("Test User", "testuser", "encodedPassword");
        Role role = new Role();
        role.setName("ROLE_MERCHANT");
        user.getRoles().add(role);

        roleRequestDTO = new UserRoleRequestDTO();
        roleRequestDTO.setUsername("testuser");
        roleRequestDTO.setRole("MERCHANT");

        statusRequestDTO = new UserStatusRequestDTO();
        statusRequestDTO.setUsername("testuser");
        statusRequestDTO.setOperation("LOCK");
    }

    @Test
    void shouldRegisterUser() {
        when(appUserRepo.findByUsername("testuser")).thenReturn(Optional.empty());
        when(appUserService.register(registrationDTO)).thenReturn(user);

        ResponseEntity<UserResponseDTO> response = userService.registerUser(registrationDTO);

        assertNotNull(response.getBody());
        assertEquals(201, response.getStatusCode().value());
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals("MERCHANT", response.getBody().getRole());
        verify(appUserRepo, times(1)).findByUsername("testuser");
        verify(appUserService, times(1)).register(registrationDTO);
    }

    @Test
    void shouldThrowConflictWhenRegisteringExistingUser() {
        when(appUserRepo.findByUsername("testuser")).thenReturn(Optional.of(new AppUser()));

        assertThrows(ConflictException.class, () -> userService.registerUser(registrationDTO));
        verify(appUserRepo, times(1)).findByUsername("testuser");
    }

    @Test
    void shouldReturnAllUsers() {
        List<AppUser> mockUsers = new ArrayList<>();
        AppUser user1 = new AppUser("Test User", "testuser", "password");
        Role role1 = new Role();
        role1.setName("ROLE_MERCHANT");
        user1.getRoles().add(role1);

        AppUser user2 = new AppUser("Another User", "anotheruser", "password");
        Role role2 = new Role();
        role2.setName("ROLE_ADMINISTRATOR");
        user2.getRoles().add(role2);

        mockUsers.add(user1);
        mockUsers.add(user2);

        when(appUserRepo.findAllByOrderByIdAsc()).thenReturn(mockUsers);

        ResponseEntity<List<UserResponseDTO>> response = userService.getAllUsers();

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, response.getBody().size());
        assertEquals("testuser", response.getBody().get(0).getUsername());
        assertEquals("MERCHANT", response.getBody().get(0).getRole());

        verify(appUserRepo, times(1)).findAllByOrderByIdAsc();
    }

    @Test
    void shouldDeleteUser() {
        when(appUserRepo.findByUsername("testuser")).thenReturn(Optional.of(user));

        ResponseEntity<UserDeletionResponseDTO> response = userService.deleteUser("testuser");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Deleted successfully!", response.getBody().getStatus());
        verify(appUserRepo, times(1)).findByUsername("testuser");
        verify(appUserRepo, times(1)).delete(user);
    }

    @Test
    void shouldThrowNotFoundWhenDeletingNonExistentUser() {
        when(appUserRepo.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deleteUser("testuser"));
        verify(appUserRepo, times(1)).findByUsername("testuser");
    }

    @Test
    void shouldChangeRole() {
        AppUser mockUser = new AppUser("Test User", "testuser", "password");
        Role mockRole = new Role();
        mockRole.setName("ROLE_ADMINISTRATOR");
        mockUser.setRoles(Set.of(mockRole));

        Role newRole = new Role();
        newRole.setName("ROLE_MERCHANT");

        when(appUserRepo.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(roleRepo.findByName("ROLE_MERCHANT")).thenReturn(Optional.of(newRole));

        ResponseEntity<UserResponseDTO> response = userService.changeRole(roleRequestDTO);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("MERCHANT", response.getBody().getRole());
        verify(appUserRepo, times(1)).findByUsername("testuser");
        verify(roleRepo, times(1)).findByName("ROLE_MERCHANT");
    }

    @Test
    void shouldChangeLockStatus() {
        AppUser mockUser = new AppUser("Test User", "testuser", "password");
        mockUser.setLocked(false);

        when(appUserRepo.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        ResponseEntity<OperationResponseDTO> response = userService.changeLockedStatus(statusRequestDTO);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().getStatus().contains("locked"));
        verify(appUserRepo, times(1)).findByUsername("testuser");
        verify(appUserRepo, times(1)).save(mockUser);
    }

    @Test
    void shouldThrowBadRequestWhenChangingLockStatusForLockedUser() {
        AppUser mockUser = new AppUser("Test User", "testuser", "password");
        mockUser.setLocked(true);

        when(appUserRepo.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        ResponseEntity<OperationResponseDTO> response = userService.changeLockedStatus(statusRequestDTO);

        assertEquals(400, response.getStatusCode().value());
        verify(appUserRepo, times(1)).findByUsername("testuser");
    }

    @Test
    void shouldThrowBadRequestForInvalidRoleChange() {
        UserRoleRequestDTO invalidRoleRequestDTO = new UserRoleRequestDTO();
        invalidRoleRequestDTO.setUsername("testuser");
        invalidRoleRequestDTO.setRole("INVALID_ROLE");

        assertThrows(BadRequestException.class, () -> userService.changeRole(invalidRoleRequestDTO));
    }

    @Test
    void shouldThrowConflictForRoleChangeToCurrentRole() {
        roleRequestDTO.setRole("MERCHANT");
        when(appUserRepo.findByUsername("testuser")).thenReturn(Optional.of(user));

        assertThrows(ConflictException.class, () -> userService.changeRole(roleRequestDTO));
        verify(appUserRepo, times(1)).findByUsername("testuser");
        verify(roleRepo, times(0)).findByName("ROLE_MERCHANT");
    }

    @Test
    void shouldThrowBadRequestWhenChangingLockStatusForAdministrator() {
        AppUser adminUser = new AppUser("Admin User", "adminuser", "password");
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMINISTRATOR");
        adminUser.setRoles(Set.of(adminRole));
        adminUser.setLocked(false);

        when(appUserRepo.findByUsername("adminuser")).thenReturn(Optional.of(adminUser));

        UserStatusRequestDTO statusRequestDTO = new UserStatusRequestDTO();
        statusRequestDTO.setUsername("adminuser");
        statusRequestDTO.setOperation("LOCK");

        BadRequestException thrown = assertThrows(BadRequestException.class, () -> userService.changeLockedStatus(statusRequestDTO));
        assertEquals("Cannot change locked status for Administrator", thrown.getMessage());
        verify(appUserRepo, times(1)).findByUsername("adminuser");
    }
}
