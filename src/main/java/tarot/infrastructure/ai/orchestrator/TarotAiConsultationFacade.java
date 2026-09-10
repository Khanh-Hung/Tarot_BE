package tarot.infrastructure.ai.orchestrator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tarot.application.dto.AccountUserDto;
import tarot.domain.entities.core.ChatMessage;
import tarot.domain.entities.core.DrawnCard;
import tarot.domain.enums.RelationshipStatus;
import tarot.domain.enums.SpreadType;
import tarot.domain.enums.Topic;
import tarot.domain.enums.ZodiacSign;
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
    private final tarot.infrastructure.ai.sanitizer.ReadingSanitizer readingSanitizer;

    @Override
    public AiReadingResult generateInitialReading(
            AccountUserDto user,
            ZodiacSign zodiacSign,
            RelationshipStatus relationshipStatus,
            String userQuestion,
            SpreadType spreadType,
            List<DrawnCard> drawnCards
    ) {
        // 1. AI phân loại chủ đề
        Topic detectedTopic = topicClassifier.classify(userQuestion);

        // 2. Dựng Prompt chi tiết cho các lá bài
        String sysInstruction = initialPromptBuilder.buildSystemInstruction();
        String userPrompt = initialPromptBuilder.buildUserPrompt(user, zodiacSign, relationshipStatus, userQuestion, detectedTopic, spreadType, drawnCards);

        // 3. AI sinh bản luận giải 4 phần
        String aiMarkdown = aiModelClient.generateContent(sysInstruction, userPrompt);

        if (aiMarkdown == null || aiMarkdown.isBlank()) {
            throw new IllegalStateException("AI Service failed to generate consultation. Please check the AI model connection.");
        }

        // 4. Lọc và chuẩn hóa văn bản (Sanitizer) trước khi trả về
        String cleanedMarkdown = readingSanitizer.sanitize(aiMarkdown, user);

        return new AiReadingResult(detectedTopic, cleanedMarkdown);
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
            throw new IllegalStateException("AI Reader is temporarily unavailable. Please try again.");
        }

        return aiReply;
    }

    @Override
    public String generateConclusionQuote(
            String userQuestion,
            List<DrawnCard> drawnCards,
            String initialReading
    ) {
        String sysInstruction = """
            Bạn là Master Tarot Reader. Nhiệm vụ của bạn là đúc kết toàn bộ quẻ bài Tarot thành ĐÚNG 1 CÂU KẾT LUẬN sâu sắc, cô đọng, giàu cảm xúc, có sức nặng chữa lành và truyền cảm hứng mạnh mẽ để người dùng đưa lên ảnh Story chia sẻ.
            Quy tắc bắt buộc:
            - Độ dài: từ 20 đến 35 từ.
            - Đúng 1 câu duy nhất bằng tiếng Việt thanh lịch, sâu sắc, không lan man.
            - Viết câu hoàn chỉnh thuần túy trong dấu ngoặc kép "...", TUYỆT ĐỐI KHÔNG giải thích, KHÔNG chào hỏi, KHÔNG dùng gạch đầu dòng.
            """;

        StringBuilder cardNames = new StringBuilder();
        if (drawnCards != null) {
            for (DrawnCard dc : drawnCards) {
                if (dc.getCard() != null) {
                    cardNames.append(dc.getCard().getNameVi())
                            .append(dc.isReversed() ? " (Ngược)" : " (Xuôi)")
                            .append(", ");
                }
            }
        }

        String userPrompt = String.format("""
            Câu hỏi của người hỏi: "%s"
            Các lá bài trên bàn: %s
            Bản luận giải tóm lược:
            %s
            
            Hãy đúc kết thành đúng 1 câu kết luận sâu sắc nhất cho người này.
            """,
            userQuestion != null ? userQuestion : "Tổng quan năng lượng",
            cardNames.toString(),
            (initialReading != null && initialReading.length() > 800)
                    ? initialReading.substring(0, 800)
                    : (initialReading != null ? initialReading : "")
        );

        String raw = aiModelClient.generateContent(sysInstruction, userPrompt);
        if (raw == null || raw.isBlank()) {
            return "Vũ trụ luôn gửi tín hiệu đến những ai biết lắng nghe trực giác của chính mình.";
        }

        String cleaned = raw.replaceAll("[*#_`>]", "").trim();
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("[\"“](.+?)[\"”]").matcher(cleaned);
        if (m.find()) {
            return m.group(1).trim();
        }
        return cleaned.replaceAll("^[\"“]|[\"”]$", "").trim();
    }
}