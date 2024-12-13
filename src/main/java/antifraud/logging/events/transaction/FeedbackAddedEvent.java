package antifraud.logging.events.transaction;

public record FeedbackAddedEvent(long transactionId, String feedback, String reviewer) {
}
