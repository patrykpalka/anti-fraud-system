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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserRegistrationRequestDTO validRegistrationRequest;
    private AppUser validUser;
    private UserRoleRequestDTO validRoleRequest;
    private UserStatusRequestDTO validStatusRequest;

    @BeforeEach
    void setUp() {
        validRegistrationRequest = createValidUserRegistrationRequest();
        validUser = createValidUser();
        validRoleRequest = createValidUserRoleRequest();
        validStatusRequest = createValidUserStatusRequest();
    }

    @Test
    @DisplayName("Should successfully register a user")
    void shouldRegisterUserSuccessfully() {
        // Arrange
        UserResponseDTO expectedResponse = new UserResponseDTO(validUser);
        when(userService.registerUser(validRegistrationRequest))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(expectedResponse));

        // Act
        ResponseEntity<UserResponseDTO> response = userController.registerUser(validRegistrationRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(userService).registerUser(validRegistrationRequest);
    }

    @Test
    @DisplayName("Should successfully get all users")
    void shouldGetAllUsersSuccessfully() {
        // Arrange
        List<UserResponseDTO> expectedUsers = List.of(new UserResponseDTO(validUser));
        when(userService.getAllUsers()).thenReturn(ResponseEntity.ok(expectedUsers));

        // Act
        ResponseEntity<List<UserResponseDTO>> response = userController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUsers, response.getBody());
        verify(userService).getAllUsers();
    }

    @Test
    @DisplayName("Should successfully delete a user")
    void shouldDeleteUserSuccessfully() {
        // Arrange
        String username = "testUser";
        UserDeletionResponseDTO expectedResponse = UserDeletionResponseDTO.ofDeletion(username);
        when(userService.deleteUser(username)).thenReturn(ResponseEntity.ok(expectedResponse));

        // Act
        ResponseEntity<UserDeletionResponseDTO> response = userController.deleteUser(username);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(userService).deleteUser(username);
    }

    @Test
    @DisplayName("Should successfully change user role")
    void shouldChangeUserRoleSuccessfully() {
        // Arrange
        UserResponseDTO expectedResponse = new UserResponseDTO(validUser);
        when(userService.changeRole(validRoleRequest)).thenReturn(ResponseEntity.ok(expectedResponse));

        // Act
        ResponseEntity<UserResponseDTO> response = userController.changeRole(validRoleRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(userService).changeRole(validRoleRequest);
    }

    @Test
    @DisplayName("Should successfully change user locked status")
    void shouldChangeUserLockedStatusSuccessfully() {
        // Arrange
        OperationResponseDTO expectedResponse = OperationResponseDTO.ofLockStatus(validUser);
        when(userService.changeLockedStatus(validStatusRequest)).thenReturn(ResponseEntity.ok(expectedResponse));

        // Act
        ResponseEntity<OperationResponseDTO> response = userController.changeLockedStatus(validStatusRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(userService).changeLockedStatus(validStatusRequest);
    }

    // Helper methods for creating test data
    private UserRegistrationRequestDTO createValidUserRegistrationRequest() {
        UserRegistrationRequestDTO request = new UserRegistrationRequestDTO();
        request.setUsername("testUser");
        request.setName("Test User");
        request.setPassword("password");
        return request;
    }

    private AppUser createValidUser() {
        AppUser user = new AppUser("Test User", "testUser", "encodedPassword");
        Role role = new Role();
        role.setName("ROLE_USER");
        user.setRoles(Set.of(role));
        return user;
    }

    private UserRoleRequestDTO createValidUserRoleRequest() {
        UserRoleRequestDTO request = new UserRoleRequestDTO();
        request.setUsername("testUser");
        request.setRole("ADMINISTRATOR");
        return request;
    }

    private UserStatusRequestDTO createValidUserStatusRequest() {
        UserStatusRequestDTO request = new UserStatusRequestDTO();
        request.setUsername("testUser");
        request.setOperation("LOCK");
        return request;
    }
}
