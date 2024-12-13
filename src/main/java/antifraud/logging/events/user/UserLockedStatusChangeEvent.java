package antifraud.logging.events.user;

public record UserLockedStatusChangeEvent(String username, boolean locked) {
}
