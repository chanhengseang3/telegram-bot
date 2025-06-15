package asia.igsaas.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final String MM_DD = "dd-MM";
    private static final String ORIGINAL_FORMAT = "yyyy-MM-dd";
    private static final DateTimeFormatter ORIGINAL_FORMATTER = DateTimeFormatter.ofPattern(ORIGINAL_FORMAT);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(MM_DD);

    public static LocalDate today() {
        return LocalDate.now(ZoneId.of("Asia/Phnom_Penh"));
    }

    public static String formatToMonthAndDate(LocalDate date) {
        return formatter.format(date);
    }

    public static LocalDate parseDate(String date) {
        return ORIGINAL_FORMATTER.parse(date, LocalDate::from);
    }
}
