package antifraud.logging.listener;

import antifraud.enums.EventNames;
import antifraud.logging.events.authentication.BruteForceAttemptEvent;
import antifraud.logging.events.authentication.FailedLoginEvent;
import antifraud.logging.events.authentication.SuccessfulLoginEvent;
import antifraud.logging.rabbitmq.RabbitMqMessagePublisher;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationEventListener.class);
    private final RabbitMqMessagePublisher rabbitMqMessagePublisher;

    @EventListener
    @Async
    public void logSuccessfulLoginEvent(SuccessfulLoginEvent event) {
        try {
            LOGGER.info("Successful login: Username: {}, IP address: {}, Timestamp: {}", event.username(), maskIp(event.ipAddress()), event.timestamp());

            rabbitMqMessagePublisher.sendEvent(EventNames.AUTHENTICATION, event);
        } catch (AmqpException ex) {
            LOGGER.error("Error publishing SuccessfulLoginEvent: {}", ex.getMessage(), ex);
        } catch (Exception ex) {
            LOGGER.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        }
    }

    @EventListener
    @Async
    public void logFailedLoginEvent(FailedLoginEvent event) {
        try {
            LOGGER.warn("Failed login: Username: {}, IP address: {}, Timestamp: {}", event.username(), maskIp(event.ipAddress()), event.timestamp());

            rabbitMqMessagePublisher.sendEvent(EventNames.AUTHENTICATION, event);
        } catch (AmqpException ex) {
            LOGGER.error("Error publishing FailedLoginEvent: {}", ex.getMessage(), ex);
        } catch (Exception ex) {
            LOGGER.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        }
    }

    @EventListener
    @Async
    public void logBruteForceAttemptEvent(BruteForceAttemptEvent event) {
        try {
            LOGGER.warn("Brute force attempt: Username: {}, IP address: {}, Timestamp: {}", event.username(), maskIp(event.ipAddress()), event.timestamp());

            rabbitMqMessagePublisher.sendEvent(EventNames.AUTHENTICATION, event);
        } catch (AmqpException ex) {
            LOGGER.error("Error publishing BruteForceAttemptEvent: {}", ex.getMessage(), ex);
        } catch (Exception ex) {
            LOGGER.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        }
    }

    private String maskIp(String ip) {
        return ip.replaceAll("(\\d+\\.\\d+\\.\\d+\\.)\\d+", "$1***");
    }
}
