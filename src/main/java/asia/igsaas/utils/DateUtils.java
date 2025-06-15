package asia.igsaas.utils;

import java.time.LocalDate;
import java.time.ZoneId;

public class DateUtils {

    public static LocalDate today() {
        return LocalDate.now(ZoneId.of("Asia/Phnom_Penh"));
    }
}
