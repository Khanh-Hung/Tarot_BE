package tarot.domain.common.datetime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public interface DateTimeProvider {
    LocalDateTime utcNow();

    default LocalDate today() {
        return utcNow().toLocalDate();
    }

    default LocalDate today(ZoneId zoneId) {
        return utcNow().atZone(ZoneId.of("UTC")).withZoneSameInstant(zoneId).toLocalDate();
    }
}