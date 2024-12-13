package antifraud.logging.events.authentication;

import java.time.LocalDateTime;

public record SuccessfulLoginEvent(String username, String ipAddress, LocalDateTime timestamp) {
}
