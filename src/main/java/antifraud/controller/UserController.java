package antifraud.controller;

import antifraud.dto.request.UserRegistrationRequestDTO;
import antifraud.dto.request.UserRoleRequestDTO;
import antifraud.dto.request.UserStatusRequestDTO;
import antifraud.dto.response.OperationResponseDTO;
import antifraud.dto.response.UserDeletionResponseDTO;
import antifraud.dto.response.UserResponseDTO;
import antifraud.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "User Management APIs", description = "APIs for managing users, roles, and access control.")
public class UserController {

    private final UserService userService;

    @PostMapping("/api/auth/user")
    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided details.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user details provided")
    })
    public ResponseEntity<UserResponseDTO> registerUser(
            @Valid @RequestBody @Parameter(description = "User registration details") UserRegistrationRequestDTO registration) {
        return userService.registerUser(registration);
    }

    @GetMapping("/api/auth/list")
    @Operation(summary = "Get all users", description = "Retrieves a list of all registered users.")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/api/auth/user/{username}")
    @Operation(summary = "Delete a user", description = "Deletes the specified user by username.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserDeletionResponseDTO> deleteUser(
            @PathVariable @Parameter(description = "Username of the user to be deleted") String username) {
        return userService.deleteUser(username);
    }

    @PutMapping("/api/auth/role")
    @Operation(summary = "Change user role", description = "Updates the role of a specified user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid role or user details")
    })
    public ResponseEntity<UserResponseDTO> changeRole(
            @Valid @RequestBody @Parameter(description = "Details of the role change") UserRoleRequestDTO roleRequest) {
        return userService.changeRole(roleRequest);
    }

    @PutMapping("/api/auth/access")
    @Operation(summary = "Change user access status", description = "Locks or unlocks a user account based on the request.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User access status updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid access status details")
    })
    public ResponseEntity<OperationResponseDTO> changeLockedStatus(
            @Valid @RequestBody @Parameter(description = "Details of the access status change") UserStatusRequestDTO statusRequest) {
        return userService.changeLockedStatus(statusRequest);
    }
}
