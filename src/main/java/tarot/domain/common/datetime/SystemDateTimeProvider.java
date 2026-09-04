package tarot.domain.common.datetime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class SystemDateTimeProvider implements DateTimeProvider {
    @Override
    public LocalDateTime utcNow() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    @Override
    public LocalDate today() {
        return utcNow().toLocalDate();
    }

    @Override
    public LocalDate today(ZoneId zoneId) {
        return utcNow().atZone(ZoneOffset.UTC).withZoneSameInstant(zoneId).toLocalDate();
    }
}