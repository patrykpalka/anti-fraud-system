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
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Tag(name = "UserController", description = "APIs for managing user accounts, roles, and access control.")
public class UserController {

    private final UserService userService;

    @PostMapping("/api/auth/user")
    @Operation(summary = "Register New User", description = "Creates a new user account with the provided registration details.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user registration details"),
            @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    public ResponseEntity<UserResponseDTO> registerUser(
            @Valid @RequestBody @Parameter(description = "User registration details", required = true) UserRegistrationRequestDTO registration) {
        return userService.registerUser(registration);
    }

    @GetMapping("/api/auth/list")
    @Operation(summary = "Get All Users", description = "Retrieves a list of all registered users in the system.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of registered users",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class))))
    })
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/api/auth/user/{username}")
    @Operation(summary = "Delete User", description = "Removes a user account from the system by username.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDeletionResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid username format")
    })
    public ResponseEntity<UserDeletionResponseDTO> deleteUser(
            @PathVariable @Parameter(description = "Username of the user to delete", required = true, example = "john_doe") String username) {
        return userService.deleteUser(username);
    }

    @PutMapping("/api/auth/role")
    @Operation(summary = "Change User Role", description = "Updates the role of an existing user. Available roles are ADMINISTRATOR, MERCHANT, and SUPPORT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User role updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid role or user details"),
            @ApiResponse(responseCode = "409", description = "User already has the requested role")
    })
    public ResponseEntity<UserResponseDTO> changeRole(
            @Valid @RequestBody @Parameter(description = "Role change details", required = true) UserRoleRequestDTO roleRequest) {
        return userService.changeRole(roleRequest);
    }

    @PutMapping("/api/auth/access")
    @Operation(summary = "Change User Access Status", description = "Locks or unlocks a user account. Administrators cannot be locked.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User access status updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OperationResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid operation or cannot modify administrator access"),
            @ApiResponse(responseCode = "409", description = "User already has the requested access status")
    })
    public ResponseEntity<OperationResponseDTO> changeLockedStatus(
            @Valid @RequestBody @Parameter(description = "Access status change details", required = true) UserStatusRequestDTO statusRequest) {
        return userService.changeLockedStatus(statusRequest);
    }
}
