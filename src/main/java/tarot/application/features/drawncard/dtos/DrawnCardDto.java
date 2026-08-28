package tarot.application.features.drawncard.dtos;

import tarot.application.features.card.queries.getcardsbydeck.CardDto;
import tarot.domain.entities.core.DrawnCard;

import java.util.UUID;

public record DrawnCardDto(
    UUID id,
    int positionIndex,
    String positionName,
    boolean isReversed,
    CardDto card
) {
    public static DrawnCardDto fromEntity(DrawnCard dc) {
        if (dc == null) return null;
        return new DrawnCardDto(
            dc.getId(),
            dc.getPositionIndex(),
            dc.getPositionName(),
            dc.isReversed(),
            CardDto.fromEntity(dc.getCard())
        );
    }
}