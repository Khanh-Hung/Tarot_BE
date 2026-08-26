package tarot.domain.common.datetime;

import java.time.LocalDateTime;

public interface DateTimeProvider {
    LocalDateTime utcNow();
}