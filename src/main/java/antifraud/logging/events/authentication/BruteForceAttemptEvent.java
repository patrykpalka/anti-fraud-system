package antifraud.logging.events.authentication;

import java.time.LocalDateTime;

public record BruteForceAttemptEvent(String username, String ipAddress, LocalDateTime timestamp) {
}
