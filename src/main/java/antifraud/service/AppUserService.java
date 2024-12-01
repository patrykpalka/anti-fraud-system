package antifraud.service;

import antifraud.dto.request.UserRegistrationRequestDTO;
import antifraud.enums.RoleNames;
import antifraud.model.AppUser;
import antifraud.model.Role;
import antifraud.repo.AppUserRepo;
import antifraud.repo.RoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepo appUserRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AppUser register(UserRegistrationRequestDTO registration) {
        AppUser appUser = registration.toEntity(passwordEncoder);

        assignRoleAndStatus(appUser);

        return appUserRepo.save(appUser);
    }

    private void assignRoleAndStatus(AppUser appUser) {
        boolean isFirstUser = appUserRepo.count() == 0;

        Role role = findRole(isFirstUser ? RoleNames.ROLE_ADMINISTRATOR : RoleNames.ROLE_MERCHANT);

        appUser.getRoles().add(role);
        appUser.setLocked(!isFirstUser);
    }

    private Role findRole(RoleNames roleName) {
        return roleRepo.findByName(roleName.toString())
                .orElseThrow(() -> new IllegalStateException("Role not found"));
    }
}
