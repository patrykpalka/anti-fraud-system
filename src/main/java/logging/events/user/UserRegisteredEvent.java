package logging.events.user;

import antifraud.model.AppUser;

public record UserRegisteredEvent(AppUser appUser) {}
