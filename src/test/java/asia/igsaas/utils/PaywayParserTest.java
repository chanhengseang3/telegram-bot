package asia.igsaas.utils;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class PaywayParserTest {
    @Test
    void testParseAmount_withDollar() {
        String text = "$250.00 paid by សុីម សោភា (*049) on Jun 14, 12:27 PM via ABA KHQR (ACLEDA Bank Plc.) at KIM MOUY. Trx. ID: 174987885037701, APV: 949058.";
        assertEquals(new BigDecimal("250.00"), PaywayParser.parseAmountStatic(text));
    }

    @Test
    void testParseAmount_noDollar() {
        String text = "250.00 paid by John Doe (*234) on Jun 14, 12:00 PM via ABA KHQR at Store. Trx. ID: 123456, APV: 654321.";
        assertEquals(new BigDecimal("250.00"), PaywayParser.parseAmountStatic(text));
    }

    @Test
    void testParseAmount_integer() {
        String text = "$100 paid by Jane Doe.";
        assertEquals(new BigDecimal("100"), PaywayParser.parseAmountStatic(text));
    }

    @Test
    void testParseAmount_noAmount() {
        String text = "Paid by Jane Doe.";
        assertEquals(BigDecimal.ZERO, PaywayParser.parseAmountStatic(text));
    }
}
