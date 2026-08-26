package tarot.application.features.reading.queries.getreadingdetail;

import tarot.application.features.chat.commands.sendchatmessage.ChatMessageDto;
import tarot.application.features.drawncard.dtos.DrawnCardDto;
import tarot.domain.entities.Reading;

import java.time.LocalDateTime;
import java.util.List;

public record ReadingDetailResponse(
    Long id,
    Long userId,
    String userQuestion,
    String topic,
    String spreadType,
    String deckCode,
    List<DrawnCardDto> drawnCards,
    String initialReading,
    List<ChatMessageDto> chatMessages,
    LocalDateTime createdAt
) {
    public static ReadingDetailResponse fromEntity(Reading r) {
        if (r == null) return null;

        List<DrawnCardDto> cards = (r.getDrawnCards() != null)
            ? r.getDrawnCards().stream().map(DrawnCardDto::fromEntity).toList()
            : List.of();

        List<ChatMessageDto> messages = (r.getChatMessages() != null)
            ? r.getChatMessages().stream().map(ChatMessageDto::fromEntity).toList()
            : List.of();

        Long uid = (r.getUser() != null) ? r.getUser().getId() : null;

        return new ReadingDetailResponse(
            r.getId(),
            uid,
            r.getUserQuestion(),
            r.getTopic().name(),
            r.getSpreadType().name(),
            r.getDeckCode().name(),
            cards,
            r.getInitialReading(),
            messages,
            r.getCreatedAt()
        );
    }
}