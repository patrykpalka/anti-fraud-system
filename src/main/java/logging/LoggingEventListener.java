package logging;

import logging.events.UserRegisteredEvent;
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
        String username = event.appUser().getUsername();
        String role = event.appUser().getRoles().iterator().next().toString();
        LOGGER.info("New user registered: Username: {}, Role: {}", username, role);
    }
}
