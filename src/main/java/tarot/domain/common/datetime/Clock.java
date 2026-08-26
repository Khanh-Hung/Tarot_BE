package tarot.domain.common.datetime;

import lombok.Setter;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

/**
 * @UtilityClass: Tương đương "public static class" bên C#.
 */
@UtilityClass
public class Clock {

    @Setter
    private static DateTimeProvider provider = new SystemDateTimeProvider();

    public static LocalDateTime now() {
        return provider.utcNow();
    }
}