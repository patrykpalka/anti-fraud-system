package antifraud.service;

import antifraud.dto.request.UserRegistrationRequestDTO;
import antifraud.dto.request.UserRoleRequestDTO;
import antifraud.dto.request.UserStatusRequestDTO;
import antifraud.dto.response.UserDeletionResponseDTO;
import antifraud.dto.response.OperationResponseDTO;
import antifraud.dto.response.UserResponseDTO;
import antifraud.enums.RoleNames;
import antifraud.model.AppUser;
import antifraud.model.Role;
import antifraud.repo.AppUserRepo;
import antifraud.repo.RoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AppUserRepo appUserRepo;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepo roleRepo;

    public ResponseEntity<?> registerUser(UserRegistrationRequestDTO registration) {
        // First validate that the username and password are not empty
        if (registration.getUsername() == null || registration.getUsername().isBlank() ||
                registration.getPassword() == null || registration.getPassword().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        if (appUserRepo.findByUsername(registration.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        AppUser appUser = registration.toEntity(passwordEncoder);

        // Assign role
        Role role;
        if (appUserRepo.count() == 0) {
            // First user gets ADMINISTRATOR role and unlocked status
            role = roleRepo.findByName(RoleNames.ROLE_ADMINISTRATOR.toString());
            appUser.setLocked(false);
        } else {
            // Subsequent users get MERCHANT role and locked status
            role = roleRepo.findByName(RoleNames.ROLE_MERCHANT.toString());
            appUser.setLocked(true);
        }
        appUser.getRoles().add(role);

        AppUser savedUser = appUserRepo.save(appUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UserResponseDTO(savedUser));
    }

    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<AppUser> users = appUserRepo.findAllByOrderByIdAsc();
        List<UserResponseDTO> userDTOs = users.stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    public ResponseEntity<UserDeletionResponseDTO> deleteUser(String username) {
        Optional<AppUser> userOptional = appUserRepo.findByUsername(username);

        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        appUserRepo.delete(userOptional.get());
        return ResponseEntity.ok(UserDeletionResponseDTO.ofDeletion(username));
    }

    public ResponseEntity<UserResponseDTO> changeRole(UserRoleRequestDTO roleRequest) {
        // Verify role change is allowed
        List<String> acceptedRoles = List.of(
                RoleNames.SUPPORT.toString(),
                RoleNames.MERCHANT.toString()
        );

        if (!acceptedRoles.contains(roleRequest.getRole())) {
            return ResponseEntity.badRequest().build();
        }

        // Find user
        Optional<AppUser> userOptional = appUserRepo.findByUsername(roleRequest.getUsername());
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        AppUser user = userOptional.get();
        String currentRole = user.getRoles().iterator().next().getName().substring(5);
        String newRole = roleRequest.getRole();

        // Check if role is different
        if (newRole.equals(currentRole)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // Update role
        Set<Role> roles = user.getRoles();
        roles.clear();
        roles.add(roleRepo.findByName("ROLE_" + newRole));

        AppUser savedUser = appUserRepo.save(user);
        return ResponseEntity.ok(new UserResponseDTO(savedUser));
    }

    public ResponseEntity<OperationResponseDTO> changeLockedStatus(UserStatusRequestDTO statusRequest) {
        Optional<AppUser> userOptional = appUserRepo.findByUsername(statusRequest.getUsername());
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        AppUser user = userOptional.get();

        // Cannot lock/unlock administrator
        if (user.getRoles().contains(roleRepo.findByName(RoleNames.ROLE_ADMINISTRATOR.toString()))) {
            return ResponseEntity.badRequest().build();
        }

        boolean newLockedStatus = statusRequest.getOperation().equals("LOCK");
        if (user.isLocked() == newLockedStatus) {
            return ResponseEntity.badRequest().build();
        }

        user.setLocked(newLockedStatus);
        AppUser savedUser = appUserRepo.save(user);
        return ResponseEntity.ok(OperationResponseDTO.ofLockStatus(savedUser));
    }
}
