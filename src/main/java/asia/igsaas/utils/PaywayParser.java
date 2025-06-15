package asia.igsaas.utils;

import asia.igsaas.domain.Currency;
import asia.igsaas.data.Result;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaywayParser {

    /**
     * Parses the amount and currency from Payway notification text.
     * Supports leading currency symbols (e.g. $, ៛) and thousands separators.
     * Returns currency as "USD" or "KHR" only.
     */
    public static Result parseAmountAndCurrency(String text) {
        String trimmed = text.trim();
        // Only parse if it starts with a currency symbol
        if (!trimmed.startsWith(Currency.KHR.getSymbol()) && !trimmed.startsWith(Currency.USD.getSymbol())) {
            return new Result(Currency.USD, BigDecimal.valueOf(0.0));
        }
        Matcher m = Pattern.compile("^([\u17DB$])?\\s*(\\d{1,3}(?:,\\d{3})*(?:\\.\\d+)?|\\d+(?:\\.\\d+)?)(?=[\\D]|$)").matcher(trimmed);
        if (m.find()) {
            String symbol = m.group(1);
            String number = m.group(2).replace(",", "");
            BigDecimal amount = new BigDecimal(number);
            Currency currency;
            if (Currency.USD.getSymbol().equals(symbol)) {
                currency = Currency.USD;
            } else if (Currency.KHR.getSymbol().equals(symbol)) {
                currency = Currency.KHR;
            } else {
                // Fallback: try to guess from text ("USD"/"៛"/"KHR"), default to USD
                if (trimmed.contains("USD") || trimmed.contains("$")) {
                    currency = Currency.USD;
                } else if (trimmed.contains("៛") || trimmed.contains("KHR")) {
                    currency = Currency.KHR;
                } else currency = Currency.USD;
            }
            return new Result(currency, amount);
        }
        return new Result(Currency.USD, BigDecimal.ZERO);
    }
}
