package antifraud.controller;

import antifraud.dto.request.UserRegistrationRequestDTO;
import antifraud.dto.request.UserRoleRequestDTO;
import antifraud.dto.request.UserStatusRequestDTO;
import antifraud.dto.response.OperationResponseDTO;
import antifraud.dto.response.UserDeletionResponseDTO;
import antifraud.dto.response.UserResponseDTO;
import antifraud.model.AppUser;
import antifraud.model.Role;
import antifraud.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() {
        UserRegistrationRequestDTO registrationRequest = new UserRegistrationRequestDTO();
        registrationRequest.setUsername("testUser");
        registrationRequest.setName("Test User");
        registrationRequest.setPassword("password");

        // Mock user and roles
        AppUser user = new AppUser("Test User", "testUser", "encodedPassword");
        Role role = new Role();
        role.setName("ROLE_USER"); // Set a role name
        user.setRoles(Set.of(role)); // Assign role to user

        // Create the response DTO
        UserResponseDTO userResponse = new UserResponseDTO(user);

        when(userService.registerUser(registrationRequest)).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(userResponse));

        // Perform the test
        ResponseEntity<UserResponseDTO> response = userController.registerUser(registrationRequest);

        // Assertions
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userResponse, response.getBody());
        verify(userService, times(1)).registerUser(registrationRequest);
    }

    @Test
    void testGetAllUsers_Success() {
        // Mock user1 with roles
        AppUser user1 = new AppUser("user1", "User One", "encodedPassword");
        Role role1 = new Role();
        role1.setName("ROLE_USER");
        user1.setRoles(Set.of(role1)); // Assign role to user1

        // Mock user2 with roles
        AppUser user2 = new AppUser("user2", "User Two", "encodedPassword");
        Role role2 = new Role();
        role2.setName("ROLE_USER");
        user2.setRoles(Set.of(role2)); // Assign role to user2

        // Create response DTOs
        UserResponseDTO userResponse1 = new UserResponseDTO(user1);
        UserResponseDTO userResponse2 = new UserResponseDTO(user2);

        List<UserResponseDTO> users = List.of(userResponse1, userResponse2);

        when(userService.getAllUsers()).thenReturn(ResponseEntity.ok(users));

        // Perform the test
        ResponseEntity<List<UserResponseDTO>> response = userController.getAllUsers();

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testDeleteUser_Success() {
        String username = "testUser";
        UserDeletionResponseDTO deletionResponse = UserDeletionResponseDTO.ofDeletion(username);

        when(userService.deleteUser(username)).thenReturn(ResponseEntity.ok(deletionResponse));

        ResponseEntity<UserDeletionResponseDTO> response = userController.deleteUser(username);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(deletionResponse, response.getBody());
        verify(userService, times(1)).deleteUser(username);
    }

    @Test
    void testChangeRole_Success() {
        // Mock role request
        UserRoleRequestDTO roleRequest = new UserRoleRequestDTO();
        roleRequest.setUsername("testUser");
        roleRequest.setRole("ADMINISTRATOR");

        // Mock AppUser with role
        AppUser user = new AppUser("testUser", "User One", "encodedPassword");
        Role role = new Role();
        role.setName("ROLE_ADMINISTRATOR");
        user.setRoles(Set.of(role)); // Assign the ADMINISTRATOR role

        // Construct the UserResponseDTO
        UserResponseDTO updatedUser = new UserResponseDTO(user);

        // Mock service call
        when(userService.changeRole(roleRequest)).thenReturn(ResponseEntity.ok(updatedUser));

        // Perform the test
        ResponseEntity<UserResponseDTO> response = userController.changeRole(roleRequest);

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedUser, response.getBody());
        verify(userService, times(1)).changeRole(roleRequest);
    }

    @Test
    void testChangeLockedStatus_Success() {
        UserStatusRequestDTO statusRequest = new UserStatusRequestDTO();
        statusRequest.setUsername("testUser");
        statusRequest.setOperation("LOCK");

        OperationResponseDTO operationResponse = OperationResponseDTO.ofLockStatus(new AppUser("user", "User One", "USER"));

        when(userService.changeLockedStatus(statusRequest)).thenReturn(ResponseEntity.ok(operationResponse));

        ResponseEntity<OperationResponseDTO> response = userController.changeLockedStatus(statusRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(operationResponse, response.getBody());
        verify(userService, times(1)).changeLockedStatus(statusRequest);
    }
}
