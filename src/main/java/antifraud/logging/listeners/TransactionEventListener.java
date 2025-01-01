package antifraud.logging.listeners;

import antifraud.enums.EventNames;
import antifraud.logging.events.transaction.FeedbackAddedEvent;
import antifraud.logging.events.transaction.FraudulentTransactionDetectedEvent;
import antifraud.logging.events.transaction.TransactionCreatedEvent;
import antifraud.messaging.RabbitMqMessagePublisher;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionEventListener.class);
    private final RabbitMqMessagePublisher rabbitMqMessagePublisher;

    @EventListener
    @Async
    public void logTransactionCreatedEvent(TransactionCreatedEvent event) {
        try {
            LOGGER.info("Transaction created by {}: Transaction ID: {}, Amount: {}, Result: {}",
                    event.reviewer(), event.transactionId(), event.amount(), event.result());

            rabbitMqMessagePublisher.sendEvent(EventNames.TRANSACTION, event);
        } catch (AmqpException ex) {
            LOGGER.error("Error publishing TransactionCreatedEvent: {}", ex.getMessage(), ex);
        } catch (Exception ex) {
            LOGGER.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        }
    }

    @EventListener
    @Async
    public void logFraudulentTransactionDetectedEvent(FraudulentTransactionDetectedEvent event) {
        try {
            LOGGER.error("Fraudulent transaction detected: Transaction ID: {}, Reasons: {}", event.transactionId(), event.reasons());

            rabbitMqMessagePublisher.sendEvent(EventNames.TRANSACTION, event);
        } catch (AmqpException ex) {
            LOGGER.error("Error publishing FraudulentTransactionDetectedEvent: {}", ex.getMessage(), ex);
        } catch (Exception ex) {
            LOGGER.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        }
    }

    @EventListener
    @Async
    public void logFeedbackAddedEvent(FeedbackAddedEvent event) {
        try {
            LOGGER.error("Feedback added by {}: Transaction ID: {}, Feedback: {}", event.reviewer(), event.transactionId(), event.feedback());

            rabbitMqMessagePublisher.sendEvent(EventNames.TRANSACTION, event);
        } catch (AmqpException ex) {
            LOGGER.error("Error publishing FeedbackAddedEvent: {}", ex.getMessage(), ex);
        } catch (Exception ex) {
            LOGGER.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        }
    }
}
