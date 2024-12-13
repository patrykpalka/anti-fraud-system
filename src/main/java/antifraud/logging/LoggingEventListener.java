package antifraud.logging;

import antifraud.logging.events.antifraud.*;
import antifraud.logging.events.user.*;
import antifraud.model.AppUser;
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
    public void logUserStatusChangedEvent(UserStatusChangedEvent event) {
        String status = event.locked() ? "locked" : "unlocked";
        LOGGER.info("User status changed: Username: {}, Status: {}", event.appUser().getUsername(), status);
    }

    @EventListener
    @Async
    public void logSuspiciousIpAddedEvent(SuspiciousIpAddedEvent event) {
        logIpAction("Suspicious IP added", event.ip());
    }

    @EventListener
    @Async
    public void logSuspiciousIpRemoveEvent(SuspiciousIpRemoveEvent event) {
        logIpAction("Suspicious IP removed", event.ip());
    }

    @EventListener
    @Async
    public void logStolenCardAddedEvent(StolenCardAddedEvent event) {
        logCardAction("Stolen card added", event.cardNumber());
    }

    @EventListener
    @Async
    public void logStolenCardRemoveEvent(StolenCardRemoveEvent event) {
        logCardAction("Stolen card removed", event.cardNumber());
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

    private void logIpAction(String action, String ip) {
        LOGGER.info("{}: IP: {}", action, maskIp(ip));
    }

    private void logCardAction(String action, String cardNumber) {
        LOGGER.info("{}: Card Number: {}", action, maskCardNumber(cardNumber));
    }

    private String maskIp(String ip) {
        return ip.replaceAll("(\\d+\\.\\d+\\.\\d+\\.)\\d+", "$1***");
    }

    private String maskCardNumber(String cardNumber) {
        return cardNumber.replaceAll("\\d(?=\\d{4})", "*");
    }
}
