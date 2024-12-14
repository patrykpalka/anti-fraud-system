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
public class AuthenticationEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationEventListener.class);

    @EventListener
    @Async
    public void logSuccessfulLoginEvent(SuccessfulLoginEvent event) {
        LOGGER.info("Successful login: Username: {}, IP address: {}, Timestamp: {}", event.username(), maskIp(event.ipAddress()), event.timestamp());
    }

    @EventListener
    @Async
    public void logFailedLoginEvent(FailedLoginEvent event) {
        LOGGER.warn("Failed login: Username: {}, IP address: {}, Timestamp: {}", event.username(), maskIp(event.ipAddress()), event.timestamp());
    }

    @EventListener
    @Async
    public void logBruteForceAttemptEvent(BruteForceAttemptEvent event) {
        LOGGER.warn("Brute force attempt: Username: {}, IP address: {}, Timestamp: {}", event.username(), maskIp(event.ipAddress()), event.timestamp());
    }

    private String maskIp(String ip) {
        return ip.replaceAll("(\\d+\\.\\d+\\.\\d+\\.)\\d+", "$1***");
    }
}
