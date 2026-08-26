package tarot.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import tarot.domain.common.BaseEntity;

@Entity
@Table(name = "reading_drawn_cards")
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class DrawnCard extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "reading_id", nullable = false)
    private Reading reading;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Column(name = "position_index", nullable = false)
    private int positionIndex;

    @Column(name = "position_name", nullable = false, length = 100)
    private String positionName;

    @Column(name = "is_reversed", nullable = false)
    private boolean isReversed;
}