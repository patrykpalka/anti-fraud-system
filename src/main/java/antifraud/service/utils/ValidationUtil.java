package antifraud.service.utils;

import antifraud.dto.transaction.TransactionRequestDTO;
import antifraud.enums.RegionNames;

import java.util.Arrays;

public class ValidationUtil {
    public static boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != 16) {
            return false;
        }

        int sum = 0;
        boolean alternate = false;

        // Iterate from right to left
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            char c = cardNumber.charAt(i);
            if (!Character.isDigit(c)) {
                return false;
            }

            int n = Character.getNumericValue(c);

            // Double every second digit
            if (alternate) {
                n *= 2;
                // If the result is greater than 9, subtract 9
                if (n > 9) {
                    n -= 9;
                }
            }

            sum += n;
            alternate = !alternate; // Flip the alternate flag
        }

        // The number is valid if the total sum is a multiple of 10
        return sum % 10 == 0;
    }

    public static boolean isValidIp(String ip) {
        String ipPattern = "^(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\\." +
                "(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\\." +
                "(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\\." +
                "(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])$";
        return ip.matches(ipPattern);
    }

    public static boolean isValidRegion(String region) {
        return Arrays.stream(RegionNames.values()).anyMatch(r -> r.name().equals(region));
    }

    public static boolean isValidTransactionInput(TransactionRequestDTO dto) {
        return dto.getAmount() > 0 &&
                isValidCardNumber(dto.getNumber()) &&
                isValidRegion(dto.getRegion());
    }
}
