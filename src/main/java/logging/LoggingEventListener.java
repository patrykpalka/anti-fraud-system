package logging;

import logging.events.user.UserRoleChangedEvent;
import logging.events.user.UserDeletedEvent;
import logging.events.user.UserRegisteredEvent;
import logging.events.user.UserStatusChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class LoggingEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingEventListener.class);

    @EventListener
    @Async
    public void logUserRegisteredEvent(UserRegisteredEvent event) {
        String username = event.appUser().getUsername();
        String role = event.appUser().getRoles().iterator().next().toString();
        LOGGER.info("New user registered: Username: {}, Role: {}", username, role);
    }

    @EventListener
    @Async
    public void logUserDeletedEvent(UserDeletedEvent event) {
        String username = event.appUser().getUsername();
        String role = event.appUser().getRoles().iterator().next().toString();
        LOGGER.info("User deleted: Username: {}, Role: {}", username, role);
    }

    @EventListener
    @Async
    public void logUserRoleChangedEvent(UserRoleChangedEvent event) {
        String username = event.appUser().getUsername();
        String currentRole = event.oldRole();
        String requestedRole = event.newRole();
        LOGGER.info("Role changed: Username: {}, Current Role: {}, Requested Role: {}", username, currentRole, requestedRole);
    }

    @EventListener
    @Async
    public void logUserStatusChangedEvent(UserStatusChangedEvent event) {
        String username = event.appUser().getUsername();
        String status = event.appUser().isLocked() ? "locked" : "unlocked";
        LOGGER.info("User status changed: Username: {}, Status: {}", username, status);
    }
}
