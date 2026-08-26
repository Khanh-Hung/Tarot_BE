package tarot.application.features.chat.commands.sendchatmessage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tarot.application.common.result.Error;
import tarot.application.common.result.Result;
import tarot.domain.entities.ChatMessage;
import tarot.domain.entities.Reading;
import tarot.infrastructure.ai.AiConsultationService;
import tarot.infrastructure.persistence.repositories.ChatMessageRepository;
import tarot.infrastructure.persistence.repositories.ReadingRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SendChatMessageHandler {

    private final ReadingRepository readingRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final AiConsultationService aiService;

    @Transactional
    public Result<ChatMessageDto> handle(Long readingId, SendChatMessageCommand command) {
        Reading reading = readingRepository.findById(readingId).orElse(null);
        if (reading == null) {
            return Result.failure(new Error("READING_NOT_FOUND", "Reading session not found with ID: " + readingId));
        }

        reading.addUserMessage(command.message());

        String aiReply = aiService.generateChatReply(
            reading.getUserQuestion(),
            reading.getDrawnCards(),
            reading.getChatMessages(),
            command.message()
        );

        reading.addAiReply(aiReply);
        readingRepository.save(reading);

        List<ChatMessage> messages = chatMessageRepository.findByReadingIdOrderByCreatedAtAsc(readingId);
        ChatMessage lastAiMsg = messages.isEmpty() ? null : messages.getLast();

        return Result.success(ChatMessageDto.fromEntity(lastAiMsg));
    }
}