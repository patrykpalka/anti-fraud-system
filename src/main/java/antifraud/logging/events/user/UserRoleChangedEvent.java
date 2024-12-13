package antifraud.logging.events.user;

import antifraud.model.AppUser;

public record UserRoleChangedEvent(AppUser appUser, String oldRole, String newRole) {}
