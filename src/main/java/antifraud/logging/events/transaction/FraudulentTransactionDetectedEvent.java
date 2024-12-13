package antifraud.logging.events.transaction;

import java.util.List;

public record FraudulentTransactionDetectedEvent(long transactionId, List<String> reasons) {
}
