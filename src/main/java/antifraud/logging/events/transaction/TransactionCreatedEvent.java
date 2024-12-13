package antifraud.logging.events.transaction;

public record TransactionCreatedEvent(long transactionId, long amount, String result, String reviewer) {
}
