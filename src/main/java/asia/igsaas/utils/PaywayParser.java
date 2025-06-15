package asia.igsaas.utils;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaywayParser {

    /**
     * Parses the amount from Payway notification text.
     * The amount is always at the start, e.g. "$100.50 ..." or "100.50 ..."
     * Should return: 100.50
     */
    public static BigDecimal parseAmountStatic(String text) {
        // Extract number at the very start, possibly preceded by a currency symbol
        Matcher m = Pattern.compile("^[^\\d]*(\\d+(?:\\.\\d+)?)(?=\\D|$)").matcher(text.trim());
        if (m.find()) {
            return new BigDecimal(m.group(1));
        }
        return BigDecimal.ZERO;
    }
}
