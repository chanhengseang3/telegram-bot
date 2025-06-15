package asia.igsaas.data;

import asia.igsaas.domain.Currency;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;

/**
 * Parses the amount from Payway notification text.
 * Supports leading currency symbols (e.g. $, ៛) and thousands separators.
 * E.g. "$250.00 ...", "៛3,600 ...", "250.00 ..."
 * Should return: 250.00 or 3600
 */

public record Result(Currency currency, BigDecimal amount) {

    @Override
    @NonNull
    public String toString() {
        return currency + "=" + amount;
    }
}
