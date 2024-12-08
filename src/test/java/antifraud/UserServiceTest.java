package antifraud;

import antifraud.dto.request.UserRegistrationRequestDTO;
import antifraud.dto.request.UserRoleRequestDTO;
import antifraud.dto.request.UserStatusRequestDTO;
import antifraud.dto.response.UserDeletionResponseDTO;
import antifraud.dto.response.OperationResponseDTO;
import antifraud.dto.response.UserResponseDTO;
import antifraud.exception.BadRequestException;
import antifraud.exception.ConflictException;
import antifraud.exception.NotFoundException;
import antifraud.model.AppUser;
import antifraud.model.Role;
import antifraud.repo.AppUserRepo;
import antifraud.repo.RoleRepo;
import antifraud.service.AppUserService;
import antifraud.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private AppUserRepo appUserRepo;

    @Mock
    private RoleRepo roleRepo;

    @Mock
    private AppUserService appUserService;

    private UserRegistrationRequestDTO registrationDTO;
    private UserRoleRequestDTO roleRequestDTO;
    private UserStatusRequestDTO statusRequestDTO;
    private AppUser user;
    private Role role;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        initTestData();
    }

    private void initTestData() {
        // Registration data setup
        registrationDTO = new UserRegistrationRequestDTO();
        registrationDTO.setUsername("testuser");
        registrationDTO.setName("Test User");
        registrationDTO.setPassword("password");

        // User setup
        user = new AppUser("Test User", "testuser", "encodedPassword");
        role = new Role();
        role.setName("ROLE_MERCHANT");
        user.getRoles().add(role);

        // Role change request setup
        roleRequestDTO = new UserRoleRequestDTO();
        roleRequestDTO.setUsername("testuser");
        roleRequestDTO.setRole("MERCHANT");

        // Status change request setup
        statusRequestDTO = new UserStatusRequestDTO();
        statusRequestDTO.setUsername("testuser");
        statusRequestDTO.setOperation("LOCK");
    }

    @Test
    void testRegisterUserSuccessfully() {
        when(appUserRepo.findByUsername("testuser")).thenReturn(Optional.empty());
        when(appUserService.register(registrationDTO)).thenReturn(user);

        ResponseEntity<UserResponseDTO> response = userService.registerUser(registrationDTO);

        assertNotNull(response.getBody());
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals("MERCHANT", response.getBody().getRole());
        verify(appUserRepo, times(1)).findByUsername("testuser");
        verify(appUserService, times(1)).register(registrationDTO);
    }

    @Test
    void testRegisterUserConflict() {
        when(appUserRepo.findByUsername("testuser")).thenReturn(Optional.of(new AppUser()));

        assertThrows(ConflictException.class, () -> userService.registerUser(registrationDTO));
        verify(appUserRepo, times(1)).findByUsername("testuser");
    }

    @Test
    void testGetAllUsersSuccessfully() {
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
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals("testuser", response.getBody().get(0).getUsername());
        assertEquals("MERCHANT", response.getBody().get(0).getRole());

        verify(appUserRepo, times(1)).findAllByOrderByIdAsc();
    }

    @Test
    void testDeleteUserSuccessfully() {
        when(appUserRepo.findByUsername("testuser")).thenReturn(Optional.of(user));

        ResponseEntity<UserDeletionResponseDTO> response = userService.deleteUser("testuser");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Deleted successfully!", response.getBody().getStatus());
        verify(appUserRepo, times(1)).findByUsername("testuser");
        verify(appUserRepo, times(1)).delete(user);
    }

    @Test
    void testDeleteUserNotFound() {
        when(appUserRepo.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deleteUser("testuser"));
        verify(appUserRepo, times(1)).findByUsername("testuser");
    }

    @Test
    void testChangeRoleSuccessfully() {
        AppUser mockUser = new AppUser("Test User", "testuser", "password");
        Role mockRole = new Role();
        mockRole.setName("ROLE_ADMINISTRATOR");
        mockUser.setRoles(Set.of(mockRole));

        Role newRole = new Role();
        newRole.setName("ROLE_MERCHANT");

        when(appUserRepo.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(roleRepo.findByName("ROLE_MERCHANT")).thenReturn(Optional.of(newRole));

        ResponseEntity<UserResponseDTO> response = userService.changeRole(roleRequestDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("MERCHANT", response.getBody().getRole());
        verify(appUserRepo, times(1)).findByUsername("testuser");
        verify(roleRepo, times(1)).findByName("ROLE_MERCHANT");
    }

    @Test
    void testChangeLockStatusSuccessfully() {
        AppUser mockUser = new AppUser("Test User", "testuser", "password");
        mockUser.setLocked(false);

        when(appUserRepo.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        ResponseEntity<OperationResponseDTO> response = userService.changeLockedStatus(statusRequestDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().getStatus().contains("locked"));
        verify(appUserRepo, times(1)).findByUsername("testuser");
        verify(appUserRepo, times(1)).save(mockUser);
    }

    @Test
    void testChangeLockStatusBadRequest() {
        AppUser mockUser = new AppUser("Admin User", "admin", "password");
        mockUser.setRoles(Set.of(new Role("ROLE_ADMINISTRATOR")));

        UserStatusRequestDTO statusRequest = new UserStatusRequestDTO();
        statusRequest.setUsername("admin");
        statusRequest.setOperation("LOCK");

        when(appUserRepo.findByUsername("admin")).thenReturn(Optional.of(mockUser));

        assertThrows(BadRequestException.class, () -> userService.changeLockedStatus(statusRequest));
        verify(appUserRepo, times(1)).findByUsername("admin");
    }

    @Test
    void testChangeRoleInvalidRole() {
        // Setup
        UserRoleRequestDTO invalidRoleRequestDTO = new UserRoleRequestDTO();
        invalidRoleRequestDTO.setUsername("testuser");
        invalidRoleRequestDTO.setRole("INVALID_ROLE");  // Invalid role

        // Test and verify
        assertThrows(BadRequestException.class, () -> userService.changeRole(invalidRoleRequestDTO));
    }

    @Test
    void testChangeRoleConflict() {
        // Setup
        UserRoleRequestDTO roleRequestDTO = new UserRoleRequestDTO();
        roleRequestDTO.setUsername("testuser");
        roleRequestDTO.setRole("MERCHANT");  // Same as the current role

        // Mock behavior
        when(appUserRepo.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Test and verify
        assertThrows(ConflictException.class, () -> userService.changeRole(roleRequestDTO));
        verify(appUserRepo, times(1)).findByUsername("testuser");
        verify(roleRepo, times(0)).findByName("ROLE_MERCHANT");  // Verify that this is not called
    }

    @Test
    void testChangeLockStatusAlreadyLocked() {
        // Setup
        UserStatusRequestDTO statusRequestDTO = new UserStatusRequestDTO();
        statusRequestDTO.setUsername("testuser");
        statusRequestDTO.setOperation("LOCK");

        // Mock behavior
        AppUser mockUser = new AppUser("Test User", "testuser", "password");
        mockUser.setLocked(true);  // Already locked
        when(appUserRepo.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        // Test and verify
        ResponseEntity<OperationResponseDTO> response = userService.changeLockedStatus(statusRequestDTO);
        assertEquals(400, response.getStatusCodeValue());  // Expecting 400 Bad Request
        verify(appUserRepo, times(1)).findByUsername("testuser");
    }
}