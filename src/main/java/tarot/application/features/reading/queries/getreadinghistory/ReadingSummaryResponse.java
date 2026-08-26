package tarot.application.features.reading.queries.getreadinghistory;

import tarot.domain.entities.Reading;
import java.time.LocalDateTime;

public record ReadingSummaryResponse(
    Long id,
    String userQuestion,
    String topic,
    String spreadType,
    String deckCode,
    int cardsCount,
    int messagesCount,
    LocalDateTime createdAt
) {
    public static ReadingSummaryResponse fromEntity(Reading r) {
        if (r == null) return null;
        return new ReadingSummaryResponse(
            r.getId(),
            r.getUserQuestion(),
            r.getTopic().name(),
            r.getSpreadType().name(),
            r.getDeckCode().name(),
            (r.getDrawnCards() != null) ? r.getDrawnCards().size() : 0,
            (r.getChatMessages() != null) ? r.getChatMessages().size() : 0,
            r.getCreatedAt()
        );
    }
}