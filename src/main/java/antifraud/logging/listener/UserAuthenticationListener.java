package antifraud.logging.listener;

import antifraud.logging.events.authentication.BruteForceAttemptEvent;
import antifraud.logging.events.authentication.FailedLoginEvent;
import antifraud.logging.events.authentication.SuccessfulLoginEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class UserAuthenticationListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthenticationListener.class);

    @EventListener
    @Async
    public void logSuccessfulLoginEvent(SuccessfulLoginEvent event) {
        LOGGER.info("Successful login: Username: {}, IP address: {}, Timestamp: {}", event.username(), maskIp(event.ipAddress()), event.timestamp());
    }

    @EventListener
    @Async
    public void logFailedLoginEvent(FailedLoginEvent event) {
        LOGGER.info("Failed login: Username: {}, IP address: {}, Timestamp: {}", event.username(), maskIp(event.ipAddress()), event.timestamp());
    }

    @EventListener
    @Async
    public void logBruteForceAttemptEvent(BruteForceAttemptEvent event) {
        LOGGER.info("Brute force attempt: Username: {}, IP address: {}, Timestamp: {}", event.username(), maskIp(event.ipAddress()), event.timestamp());
    }

    private String maskIp(String ip) {
        return ip.replaceAll("(\\d+\\.\\d+\\.\\d+\\.)\\d+", "$1***");
    }
}
