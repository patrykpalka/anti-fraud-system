package antifraud.logging.events.user;

import antifraud.model.AppUser;

public record UserDeletedEvent(AppUser appUser) {
}
