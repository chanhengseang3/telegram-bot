package asia.igsaas.utils;

import asia.igsaas.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class StorageUtils {
    private static final ConcurrentHashMap<Long, HashMap<LocalDate, HashMap<Currency, BigDecimal>>> DB = new ConcurrentHashMap<>();

    public synchronized static void addIncoming(Long groupId, BigDecimal amount, Currency currency) {
        HashMap<LocalDate, HashMap<Currency, BigDecimal>> group = DB.get(groupId);
        if (group == null) {
            group = new HashMap<>();
        }
        HashMap<Currency, BigDecimal> day = group.get(DateUtils.today());
        if (day == null) {
            day = new HashMap<>();
        }
        day.merge(currency, amount, BigDecimal::add);
        group.put(DateUtils.today(), day);
        DB.put(groupId, group);
    }

    public synchronized static HashMap<Currency, BigDecimal> getTotalIncoming(Long groupId, LocalDate localDate) {
        HashMap<LocalDate, HashMap<Currency, BigDecimal>> group = DB.get(groupId);
        return group.get(localDate);
    }
}
