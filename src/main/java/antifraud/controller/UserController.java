package antifraud.controller;

import antifraud.dto.request.UserRegistrationRequestDTO;
import antifraud.dto.request.UserRoleRequestDTO;
import antifraud.dto.request.UserStatusRequestDTO;
import antifraud.dto.response.OperationResponseDTO;
import antifraud.dto.response.UserDeletionResponseDTO;
import antifraud.dto.response.UserResponseDTO;
import antifraud.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/auth/user")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRegistrationRequestDTO registration) {
        return userService.registerUser(registration);
    }

    @GetMapping("/api/auth/list")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/api/auth/user/{username}")
    public ResponseEntity<UserDeletionResponseDTO> deleteUser(@PathVariable String username) {
        return userService.deleteUser(username);
    }

    @PutMapping("/api/auth/role")
    public ResponseEntity<UserResponseDTO> changeRole(@Valid @RequestBody UserRoleRequestDTO roleRequest) {
        return userService.changeRole(roleRequest);
    }

    @PutMapping("/api/auth/access")
    public ResponseEntity<OperationResponseDTO> changeLockedStatus(@Valid @RequestBody UserStatusRequestDTO statusRequest) {
        return userService.changeLockedStatus(statusRequest);
    }
}
