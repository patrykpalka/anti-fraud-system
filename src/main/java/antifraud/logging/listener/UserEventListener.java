package antifraud.logging.listener;

import antifraud.logging.events.user.UserDeletedEvent;
import antifraud.logging.events.user.UserRegisteredEvent;
import antifraud.logging.events.user.UserRoleChangedEvent;
import antifraud.logging.events.user.UserLockedStatusChangeEvent;
import antifraud.model.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserEventListener.class);

    @EventListener
    @Async
    public void logUserRegisteredEvent(UserRegisteredEvent event) {
        logUserAction("New user registered", event.appUser(), null, null);
    }

    @EventListener
    @Async
    public void logUserDeletedEvent(UserDeletedEvent event) {
        logUserAction("User deleted", event.appUser(), null, null);
    }

    @EventListener
    @Async
    public void logUserRoleChangedEvent(UserRoleChangedEvent event) {
        logUserAction("Role changed", event.appUser(), event.oldRole(), event.newRole());
    }

    @EventListener
    @Async
    public void logUserStatusChangedEvent(UserLockedStatusChangeEvent event) {
        String status = event.locked() ? "locked" : "unlocked";
        LOGGER.info("User status changed: Username: {}, Status: {}", event.username(), status);
    }

    private void logUserAction(String action, AppUser user, String oldRole, String newRole) {
        String username = user.getUsername();
        String roles = String.join(", ", user.getRoles().stream().map(Object::toString).toArray(String[]::new));
        if (oldRole != null && newRole != null) {
            LOGGER.info("{}: Username: {}, Old Role: {}, New Role: {}", action, username, oldRole, newRole);
        } else {
            LOGGER.info("{}: Username: {}, Roles: {}", action, username, roles);
        }
    }
}
