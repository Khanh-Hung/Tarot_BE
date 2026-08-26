package tarot.domain.common.events;

import tarot.domain.common.datetime.Clock;
import java.time.LocalDateTime;

public interface DomainEvent {
    default LocalDateTime occurredOn() {
        return Clock.now();
    }
}