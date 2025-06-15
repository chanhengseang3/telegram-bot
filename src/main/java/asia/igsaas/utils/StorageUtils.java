package asia.igsaas.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;

public class StorageUtils {
    private static final ConcurrentHashMap<Long, ConcurrentHashMap<LocalDate, BigDecimal>> DB = new ConcurrentHashMap<>();

    public synchronized static void addIncoming(Long groupId, BigDecimal amount) {
        ConcurrentHashMap<LocalDate, BigDecimal> map = DB.get(groupId);
        if (map == null) {
            map = new ConcurrentHashMap<>();
        }
        map.merge(DateUtils.today(), amount, BigDecimal::add);
        DB.put(groupId, map);
    }

    public synchronized static BigDecimal getTotalIncoming(Long groupId, LocalDate localDate) {
        ConcurrentHashMap<LocalDate, BigDecimal> map = DB.get(groupId);
        return map == null ? BigDecimal.ZERO : map.get(localDate);
    }
}
