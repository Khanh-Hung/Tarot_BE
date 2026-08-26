package tarot.domain.common;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tarot.domain.common.events.DomainEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@MappedSuperclass
@NoArgsConstructor
@SuperBuilder
public abstract class AggregateRoot extends BaseEntity {

    @Transient
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    protected void registerEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    public void clearDomainEvents() {
        this.domainEvents.clear();
    }
}