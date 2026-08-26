package tarot.application.features.chat.commands.sendchatmessage;

import tarot.domain.entities.ChatMessage;
import java.time.LocalDateTime;

public record ChatMessageDto(
    Long id,
    String sender,
    String content,
    LocalDateTime createdAt
) {
    public static ChatMessageDto fromEntity(ChatMessage msg) {
        if (msg == null) return null;
        return new ChatMessageDto(
            msg.getId(),
            msg.getSender().name(),
            msg.getContent(),
            msg.getCreatedAt()
        );
    }
}