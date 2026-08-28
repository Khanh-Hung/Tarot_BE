package tarot.application.features.reading.commands.createreading;

import tarot.application.features.drawncard.dtos.DrawnCardDto;
import tarot.domain.entities.core.Reading;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateReadingResponse(
    UUID id,
    String topic,
    String spreadType,
    String deckCode,
    List<DrawnCardDto> drawnCards,
    String initialReading,
    LocalDateTime createdAt
) {
    public static CreateReadingResponse fromEntity(Reading r) {
        if (r == null) return null;
        List<DrawnCardDto> cards = (r.getDrawnCards() != null)
            ? r.getDrawnCards().stream().map(DrawnCardDto::fromEntity).toList()
            : List.of();

        return new CreateReadingResponse(
            r.getId(),
            r.getTopic().name(),
            r.getSpreadType().name(),
            r.getDeckCode().name(),
            cards,
            r.getInitialReading(),
            r.getCreatedAt()
        );
    }
}