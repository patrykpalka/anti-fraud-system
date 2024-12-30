package antifraud.utils;

import antifraud.constants.Constants;
import antifraud.enums.TransactionType;
import antifraud.model.Transaction;

public class ConstantsUtil {

    public static void updateTransactionLimit(String feedback, Transaction transaction) {
        TransactionType feedbackTransactionType = TransactionType.valueOf(feedback);
        TransactionType resultTransactionType = TransactionType.valueOf(transaction.getResult());
        long amount = transaction.getAmount();

        if (feedbackTransactionType == TransactionType.ALLOWED) {
            handleAllowedFeedback(resultTransactionType, amount);
        }
        else if (feedbackTransactionType == TransactionType.MANUAL_PROCESSING) {
            handleManualFeedback(resultTransactionType, amount);
        }
        else if (feedbackTransactionType == TransactionType.PROHIBITED) {
            handleProhibitedFeedback(resultTransactionType, amount);
        }
    }

    private static void handleAllowedFeedback(TransactionType type, long amount) {
        if (type == TransactionType.MANUAL_PROCESSING) {
            Constants.MAX_ALLOWED = calculateNewLimit(Constants.MAX_ALLOWED, amount, true);
        } else if (type == TransactionType.PROHIBITED) {
            Constants.MAX_ALLOWED = calculateNewLimit(Constants.MAX_ALLOWED, amount, true);
            Constants.MAX_MANUAL_PROCESSING = calculateNewLimit(Constants.MAX_MANUAL_PROCESSING, amount, true);
        }
    }

    private static void handleManualFeedback(TransactionType type, long amount) {
        if (type == TransactionType.ALLOWED) {
            Constants.MAX_ALLOWED = calculateNewLimit(Constants.MAX_ALLOWED, amount, false);
        } else if (type == TransactionType.PROHIBITED) {
            Constants.MAX_MANUAL_PROCESSING = calculateNewLimit(Constants.MAX_MANUAL_PROCESSING, amount, true);
        }
    }

    private static void handleProhibitedFeedback(TransactionType type, long amount) {
        if (type == TransactionType.ALLOWED) {
            Constants.MAX_ALLOWED = calculateNewLimit(Constants.MAX_ALLOWED, amount, false);
            Constants.MAX_MANUAL_PROCESSING = calculateNewLimit(Constants.MAX_MANUAL_PROCESSING, amount, false);
        } else if (type == TransactionType.MANUAL_PROCESSING) {
            Constants.MAX_MANUAL_PROCESSING = calculateNewLimit(Constants.MAX_MANUAL_PROCESSING, amount, false);
        }
    }

    private static long calculateNewLimit(long currentLimit, long valueFromTransaction, boolean increase) {
        double factor = increase ? 1 : -1;
        double updatedValue = 0.8 * currentLimit + factor * 0.2 * valueFromTransaction;
        return (long) Math.ceil(updatedValue);
    }
}
