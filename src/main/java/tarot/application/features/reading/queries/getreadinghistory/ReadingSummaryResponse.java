package tarot.application.features.reading.queries.getreadinghistory;

import tarot.domain.entities.Reading;
import java.time.LocalDateTime;
import java.util.UUID;

public record ReadingSummaryResponse(
    UUID id,
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
            (r.getTopic() != null) ? r.getTopic().name() : "GENERAL_GUIDANCE",
            (r.getSpreadType() != null) ? r.getSpreadType().name() : "PAST_PRESENT_FUTURE",
            (r.getDeckCode() != null) ? r.getDeckCode().name() : "RIDER_WAITE_CLASSIC",
            (r.getDrawnCards() != null) ? r.getDrawnCards().size() : 0,
            (r.getChatMessages() != null) ? r.getChatMessages().size() : 0,
            r.getCreatedAt()
        );
    }
}