package antifraud.logging.listener;

import antifraud.logging.events.antifraud.StolenCardAddedEvent;
import antifraud.logging.events.antifraud.StolenCardRemoveEvent;
import antifraud.logging.events.antifraud.SuspiciousIpAddedEvent;
import antifraud.logging.events.antifraud.SuspiciousIpRemoveEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionEventListener.class);

    @EventListener
    @Async
    public void logSuspiciousIpAddedEvent(SuspiciousIpAddedEvent event) {
        logIpAction("Suspicious IP added", event.ip(), "WARN");
    }

    @EventListener
    @Async
    public void logSuspiciousIpRemoveEvent(SuspiciousIpRemoveEvent event) {
        logIpAction("Suspicious IP removed", event.ip(), "INFO");
    }

    @EventListener
    @Async
    public void logStolenCardAddedEvent(StolenCardAddedEvent event) {
        logCardAction("Stolen card added", event.cardNumber(), "WARN");
    }

    @EventListener
    @Async
    public void logStolenCardRemoveEvent(StolenCardRemoveEvent event) {
        logCardAction("Stolen card removed", event.cardNumber(), "INFO");
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
