package tarot.domain.common.datetime;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class SystemDateTimeProvider implements DateTimeProvider {
    @Override
    public LocalDateTime utcNow() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }
}