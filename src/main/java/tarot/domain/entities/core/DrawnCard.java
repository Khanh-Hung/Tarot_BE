package tarot.domain.entities.core;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import tarot.domain.common.BaseEntity;

@Entity
@Table(name = "\"ReadingDrawnCards\"")
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class DrawnCard extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "\"ReadingId\"", nullable = false)
    private Reading reading;

    @ManyToOne
    @JoinColumn(name = "\"CardId\"", nullable = true)
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private Card card;

    @Column(name = "\"PositionIndex\"", nullable = false)
    private int positionIndex;

    @Column(name = "\"PositionName\"", nullable = false, length = 100)
    private String positionName;

    @Column(name = "\"IsReversed\"", nullable = false)
    private boolean isReversed;
}