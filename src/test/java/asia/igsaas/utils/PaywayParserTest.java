package asia.igsaas.utils;

import asia.igsaas.domain.Currency;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class PaywayParserTest {
    @Test
    void testParseAmountAndCurrency_usd() {
        String text = "$250.00 paid by សុីម សោភា (*049) on Jun 14, 12:27 PM via ABA KHQR (ACLEDA Bank Plc.) at KIM MOUY. Trx. ID: 174987885037701, APV: 949058.";
        var result = PaywayParser.parseAmountAndCurrency(text);
        assertEquals(new BigDecimal("250.00"), result.amount());
        assertEquals(Currency.USD, result.currency());
    }

    @Test
    void testParseAmountAndCurrency_khr() {
        String text = "\u17DB3,600 paid by Pheap Sophea (*560) on Jun 13, 02:57 PM via ABA KHQR (ACLEDA Bank Plc.) at MISS 16 by T.KIM. Trx. ID: 174980145355424, APV: 871887.";
        var result = PaywayParser.parseAmountAndCurrency(text);
        assertEquals(new BigDecimal("3600"), result.amount());
        assertEquals(Currency.KHR, result.currency());
    }

    @Test
    void testParseAmountAndCurrency_fallback_usd() {
        String text = "250.00 USD paid by John Doe.";
        var result = PaywayParser.parseAmountAndCurrency(text);
        assertNotEquals(new BigDecimal("250.00"), result.amount());
        assertNull( result.currency());
    }

    @Test
    void testParseAmountAndCurrency_fallback_khr() {
        String text = "3600 KHR paid by John Doe.";
        var result = PaywayParser.parseAmountAndCurrency(text);
        assertNotEquals(new BigDecimal("3600"), result.amount());
        assertNull(result.currency());
    }
}
