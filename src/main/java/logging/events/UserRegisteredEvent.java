package logging.events;

import antifraud.model.AppUser;

public record UserRegisteredEvent(AppUser appUser) {}
