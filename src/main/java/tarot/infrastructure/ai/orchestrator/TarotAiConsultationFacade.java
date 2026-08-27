package tarot.infrastructure.ai.orchestrator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tarot.domain.entities.ChatMessage;
import tarot.domain.entities.DrawnCard;
import tarot.domain.entities.User;
import tarot.domain.enums.SpreadType;
import tarot.domain.enums.Topic;
import tarot.infrastructure.ai.AiConsultationService;
import tarot.infrastructure.ai.core.AiModelClient;
import tarot.infrastructure.ai.core.AiReadingResult;
import tarot.infrastructure.ai.core.TopicClassifier;
import tarot.infrastructure.ai.prompts.ChatConsultationPromptBuilder;
import tarot.infrastructure.ai.prompts.InitialReadingPromptBuilder;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TarotAiConsultationFacade implements AiConsultationService {

    private final AiModelClient aiModelClient;
    private final TopicClassifier topicClassifier;
    private final InitialReadingPromptBuilder initialPromptBuilder;
    private final ChatConsultationPromptBuilder chatPromptBuilder;

    @Override
    public AiReadingResult generateInitialReading(
            User user,
            String userQuestion,
            SpreadType spreadType,
            List<DrawnCard> drawnCards
    ) {
        // 1. AI phân loại chủ đề
        Topic detectedTopic = topicClassifier.classify(userQuestion);

        // 2. Dựng Prompt chi tiết cho các lá bài
        String sysInstruction = initialPromptBuilder.buildSystemInstruction();
        String userPrompt = initialPromptBuilder.buildUserPrompt(user, userQuestion, detectedTopic, spreadType, drawnCards);

        // 3. AI sinh bản luận giải 4 phần
        String aiMarkdown = aiModelClient.generateContent(sysInstruction, userPrompt);

        if (aiMarkdown == null || aiMarkdown.isBlank()) {
            throw new IllegalStateException("AI Service không thể sinh bản luận giải. Vui lòng kiểm tra lại kết nối API của mô hình AI.");
        }

        return new AiReadingResult(detectedTopic, aiMarkdown);
    }

    @Override
    public String generateChatReply(
            String userQuestion,
            List<DrawnCard> drawnCards,
            List<ChatMessage> history,
            String userNewMessage
    ) {
        String sysInstruction = chatPromptBuilder.buildSystemInstruction();
        String userPrompt = chatPromptBuilder.buildUserPrompt(userQuestion, drawnCards, history, userNewMessage);

        String aiReply = aiModelClient.generateContent(sysInstruction, userPrompt);
        if (aiReply == null || aiReply.isBlank()) {
            throw new IllegalStateException("AI Reader tạm thời không thể hồi đáp. Vui lòng thử lại.");
        }

        return aiReply;
    }
}