package tarot.application.features.chat.commands.sendchatmessage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tarot.application.common.result.Error;
import tarot.application.common.result.Result;
import tarot.application.dto.AccountUserDto;
import tarot.application.interfaces.AccountServiceClient;
import tarot.domain.entities.core.ChatMessage;
import tarot.domain.entities.core.Reading;
import tarot.domain.enums.MessageSender;
import tarot.infrastructure.ai.AiConsultationService;
import tarot.infrastructure.persistence.repositories.core.ChatMessageRepository;
import tarot.infrastructure.persistence.repositories.core.ReadingRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SendChatMessageHandler {

    private final ReadingRepository readingRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final AccountServiceClient accountServiceClient;
    private final AiConsultationService aiService;

    @Transactional
    public Result<ChatMessageDto> handle(UUID readingId, SendChatMessageCommand command) {
        Reading reading = readingRepository.findById(readingId).orElse(null);
        if (reading == null) {
            return Result.failure(new Error("READING_NOT_FOUND", "Reading session not found with ID: " + readingId));
        }

        // Enforce Email Verification requirement for AI Chat
        if (reading.getUserId() != null) {
            Optional<AccountUserDto> userOpt = accountServiceClient.getUser(reading.getUserId());
            if (userOpt.isPresent() && !userOpt.get().isEmailVerified()) {
                return Result.failure(new Error("EMAIL_NOT_VERIFIED", "Please verify your email address to unlock chat consultation with AI Reader."));
            }
        }

        // 1. Lưu tin nhắn của User
        ChatMessage userMsg = ChatMessage.builder()
                .reading(reading)
                .sender(MessageSender.USER)
                .content(command.message())
                .build();
        chatMessageRepository.save(userMsg);

        // 2. Lấy lịch sử chat hiện tại
        List<ChatMessage> history = chatMessageRepository.findByReadingIdOrderByCreatedAtAsc(readingId);

        // 3. Gọi AI Reader
        String aiReply = aiService.generateChatReply(
            reading.getUserQuestion(),
            reading.getDrawnCards(),
            history,
            command.message()
        );

        // 4. Lưu tin nhắn của AI Reader
        ChatMessage aiMsg = ChatMessage.builder()
                .reading(reading)
                .sender(MessageSender.AI_READER)
                .content(aiReply)
                .build();
        ChatMessage savedAiMsg = chatMessageRepository.save(aiMsg);

        return Result.success(ChatMessageDto.fromEntity(savedAiMsg));
    }
}