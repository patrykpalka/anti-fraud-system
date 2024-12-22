package antifraud.logging.listener;

import antifraud.enums.EventNames;
import antifraud.logging.events.antifraud.StolenCardAddedEvent;
import antifraud.logging.events.antifraud.StolenCardRemoveEvent;
import antifraud.logging.events.antifraud.SuspiciousIpAddedEvent;
import antifraud.logging.events.antifraud.SuspiciousIpRemoveEvent;
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
public class AntiFraudEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AntiFraudEventListener.class);
    private final RabbitMqMessagePublisher rabbitMqMessagePublisher;

    @EventListener
    @Async
    public void logSuspiciousIpAddedEvent(SuspiciousIpAddedEvent event) {
        try {
            logIpAction("Suspicious IP added", event.ip(), "WARN");

            rabbitMqMessagePublisher.sendEvent(EventNames.ANTIFRAUD.toString(), event);
        } catch (AmqpException ex) {
            LOGGER.error("Error publishing SuspiciousIpAddedEvent: {}", ex.getMessage(), ex);
        }
    }

    @EventListener
    @Async
    public void logSuspiciousIpRemoveEvent(SuspiciousIpRemoveEvent event) {
        try {
            logIpAction("Suspicious IP removed", event.ip(), "INFO");

            rabbitMqMessagePublisher.sendEvent(EventNames.ANTIFRAUD.toString(), event);
        } catch (AmqpException ex) {
            LOGGER.error("Error publishing SuspiciousIpRemoveEvent: {}", ex.getMessage(), ex);
        }
    }

    @EventListener
    @Async
    public void logStolenCardAddedEvent(StolenCardAddedEvent event) {
        try {
            logCardAction("Stolen card added", event.cardNumber(), "WARN");

            rabbitMqMessagePublisher.sendEvent(EventNames.ANTIFRAUD.toString(), event);
        } catch (AmqpException ex) {
            LOGGER.error("Error publishing StolenCardAddedEvent: {}", ex.getMessage(), ex);
        }
    }

    @EventListener
    @Async
    public void logStolenCardRemoveEvent(StolenCardRemoveEvent event) {
        try {
            logCardAction("Stolen card removed", event.cardNumber(), "INFO");

            rabbitMqMessagePublisher.sendEvent(EventNames.ANTIFRAUD.toString(), event);
        } catch (AmqpException ex) {
            LOGGER.error("Error publishing StolenCardRemoveEvent: {}", ex.getMessage(), ex);
        }
    }

    private void logIpAction(String action, String ip, String logLevel) {
        if ("WARN".equals(logLevel)) {
            LOGGER.warn("{}: IP: {}", action, maskIp(ip));
        } else {
            LOGGER.info("{}: IP: {}", action, maskIp(ip));
        }
    }

    private void logCardAction(String action, String cardNumber, String logLevel) {
        if ("WARN".equals(logLevel)) {
            LOGGER.warn("{}: Card Number: {}", action, maskCardNumber(cardNumber));
        } else {
            LOGGER.info("{}: Card Number: {}", action, maskCardNumber(cardNumber));
        }
    }

    private String maskIp(String ip) {
        return ip.replaceAll("(\\d+\\.\\d+\\.\\d+\\.)\\d+", "$1***");
    }

    private String maskCardNumber(String cardNumber) {
        return cardNumber.replaceAll("\\d(?=\\d{4})", "*");
    }
}
