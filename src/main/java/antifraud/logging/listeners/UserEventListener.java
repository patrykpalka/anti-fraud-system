package antifraud.logging.listeners;

import antifraud.enums.EventNames;
import antifraud.logging.events.user.UserDeletedEvent;
import antifraud.logging.events.user.UserRegisteredEvent;
import antifraud.logging.events.user.UserRoleChangedEvent;
import antifraud.logging.events.user.UserLockedStatusChangeEvent;
import antifraud.messaging.RabbitMqMessagePublisher;
import antifraud.model.AppUser;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserEventListener.class);
    private final RabbitMqMessagePublisher rabbitMqMessagePublisher;

    @EventListener
    @Async
    public void logUserRegisteredEvent(UserRegisteredEvent event) {
        try {
            logUserAction("New user registered", event.appUser(), null, null);

            rabbitMqMessagePublisher.sendEvent(EventNames.USER, event);
        } catch (AmqpException ex) {
            LOGGER.error("Error publishing UserRegisteredEvent: {}", ex.getMessage(), ex);
        } catch (Exception ex) {
            LOGGER.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        }
    }

    @EventListener
    @Async
    public void logUserDeletedEvent(UserDeletedEvent event) {
        try {
            logUserAction("User deleted", event.appUser(), null, null);

            rabbitMqMessagePublisher.sendEvent(EventNames.USER, event);
        } catch (AmqpException ex) {
            LOGGER.error("Error publishing UserDeletedEvent: {}", ex.getMessage(), ex);
        } catch (Exception ex) {
            LOGGER.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        }
    }

    @EventListener
    @Async
    public void logUserRoleChangedEvent(UserRoleChangedEvent event) {
        try {
            logUserAction("Role changed", event.appUser(), event.oldRole(), event.newRole());

            rabbitMqMessagePublisher.sendEvent(EventNames.USER, event);
        } catch (AmqpException ex) {
            LOGGER.error("Error publishing UserRoleChangedEvent: {}", ex.getMessage(), ex);
        } catch (Exception ex) {
            LOGGER.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        }
    }

    @EventListener
    @Async
    public void logUserStatusChangedEvent(UserLockedStatusChangeEvent event) {
        try {
            String status = event.locked() ? "locked" : "unlocked";
            LOGGER.info("User status changed: Username: {}, Status: {}", event.username(), status);

            rabbitMqMessagePublisher.sendEvent(EventNames.USER, event);
        } catch (AmqpException ex) {
            LOGGER.error("Error publishing UserLockedStatusChangeEvent: {}", ex.getMessage(), ex);
        } catch (Exception ex) {
            LOGGER.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        }
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
