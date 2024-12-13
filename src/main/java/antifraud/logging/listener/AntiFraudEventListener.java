package antifraud.logging.listener;

import antifraud.logging.events.transaction.FeedbackAddedEvent;
import antifraud.logging.events.transaction.FraudulentTransactionDetectedEvent;
import antifraud.logging.events.transaction.TransactionCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AntiFraudEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AntiFraudEventListener.class);

    @EventListener
    @Async
    public void logTransactionCreatedEvent(TransactionCreatedEvent event) {
        LOGGER.info("Transaction created by {}: Transaction ID: {}, Amount: {}, Result: {}",
                event.reviewer(), event.transactionId(), event.amount(), event.result());
    }

    @EventListener
    @Async
    public void logFraudulentTransactionDetectedEvent(FraudulentTransactionDetectedEvent event) {
        LOGGER.info("Fraudulent transaction detected: Transaction ID: {}, Reasons: {}", event.transactionId(), event.reasons());
    }

    @EventListener
    @Async
    public void logFeedbackAddedEvent(FeedbackAddedEvent event) {
        LOGGER.info("Feedback added by {}: Transaction ID: {}, Feedback: {}", event.reviewer(), event.transactionId(), event.feedback());
    }
}
