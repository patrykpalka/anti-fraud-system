package antifraud.logging.events.user;

import antifraud.model.AppUser;

public record UserStatusChangedEvent(AppUser appUser, boolean locked) {
}
