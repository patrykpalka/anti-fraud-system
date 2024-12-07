package antifraud.service;

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
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static antifraud.service.utils.ValidationUtil.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AppUserRepo appUserRepo;
    private final RoleRepo roleRepo;
    private final AppUserService appUserService;

    @Transactional
    public ResponseEntity<UserResponseDTO> registerUser(UserRegistrationRequestDTO registration) {
        if (appUserRepo.findByUsername(registration.getUsername()).isPresent()) {
            throw new ConflictException("Username already exists");
        }

        AppUser registeredUser = appUserService.register(registration);

        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponseDTO(registeredUser));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = appUserRepo.findAllByOrderByIdAsc().stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    @Transactional
    public ResponseEntity<UserDeletionResponseDTO> deleteUser(String username) {
        AppUser user = appUserRepo.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        appUserRepo.delete(user);

        return ResponseEntity.ok(UserDeletionResponseDTO.ofDeletion(username));
    }

    @Transactional
    public ResponseEntity<UserResponseDTO> changeRole(UserRoleRequestDTO roleRequest) {
        if (!isValidUserRoleChange(roleRequest.getRole())) {
            throw new BadRequestException("Invalid role");
        }

        AppUser user = appUserRepo.findByUsername(roleRequest.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));

        String currentRoleName = getCurrentRoleName(user);
        String requestedRoleName = roleRequest.getRole();

        if (requestedRoleName.equals(currentRoleName)) {
            throw new ConflictException("Role already exists");
        }

        updateAndSaveRequestedRole(user, requestedRoleName);

        return ResponseEntity.ok(new UserResponseDTO(user));
    }

    private String getCurrentRoleName(AppUser user) {
        return user.getRoles().stream()
                .findFirst()
                .map(role -> role.getName().substring(5))
                .orElseThrow(() -> new NotFoundException("User has no roles assigned"));
    }

    private void updateAndSaveRequestedRole(AppUser user, String role) {
        Role newRole = roleRepo.findByName("ROLE_" + role)
                .orElseThrow(() -> new NotFoundException("Role not found"));

        user.setRoles(new HashSet<>(Set.of(newRole)));

        appUserRepo.save(user);
    }

    @Transactional
    public ResponseEntity<OperationResponseDTO> changeLockedStatus(UserStatusRequestDTO statusRequest) {
        AppUser user = appUserRepo.findByUsername(statusRequest.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (isUserAnAdministrator(user)) {
            throw new BadRequestException("Cannot change locked status for Administrator");
        }

        boolean newLockedStatus = statusRequest.getOperation().equals("LOCK");
        if (user.isLocked() == newLockedStatus) {
            return ResponseEntity.badRequest().build();
        }

        user.setLocked(newLockedStatus);
        appUserRepo.save(user);

        return ResponseEntity.ok(OperationResponseDTO.ofLockStatus(user));
    }
}
