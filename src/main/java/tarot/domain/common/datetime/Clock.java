package tarot.domain.common.datetime;

import lombok.Setter;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

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

    public static LocalDate today() {
        return provider.today();
    }

    public static LocalDate today(ZoneId zoneId) {
        return provider.today(zoneId);
    }
}