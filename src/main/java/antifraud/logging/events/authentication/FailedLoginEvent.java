package antifraud.logging.events.authentication;

import java.time.LocalDateTime;

public record FailedLoginEvent(String username, String ipAddress, LocalDateTime timestamp) {
}
