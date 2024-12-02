package antifraud.service.utils;

import antifraud.dto.request.UserRegistrationRequestDTO;
import antifraud.enums.RoleNames;
import antifraud.model.AppUser;
import antifraud.model.Role;

import java.util.List;

public class ValidationUtil {
    public static boolean isValidRegistrationInput(UserRegistrationRequestDTO registration) {
        return registration.getUsername() != null && !registration.getUsername().isBlank() &&
                registration.getPassword() != null && !registration.getPassword().isBlank();
    }

    public static boolean isValidUserRoleChange(String role) {
        List<String> validRoles = List.of(RoleNames.SUPPORT.toString(), RoleNames.MERCHANT.toString());
        return validRoles.contains(role);
    }

    public static boolean isUserAnAdministrator(AppUser user) {
        return user.getRoles().contains(new Role(RoleNames.ROLE_ADMINISTRATOR.toString()));
    }
}
