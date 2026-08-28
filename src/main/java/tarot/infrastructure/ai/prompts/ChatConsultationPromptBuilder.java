package tarot.infrastructure.ai.prompts;

import org.springframework.stereotype.Component;
import tarot.domain.entities.core.ChatMessage;
import tarot.domain.entities.core.DrawnCard;

import java.util.List;

@Component
public class ChatConsultationPromptBuilder {

    public String buildSystemInstruction() {
        return """
            Bạn là Oracle AI Reader. Bạn đang trong phiên tham vấn trò chuyện 1-1 nối tiếp với người dùng.
            Hãy bám sát các lá bài Tarot đang nằm trên bàn để trả lời, thấu cảm và hướng dẫn người dùng giải tỏa khúc mắc.
            """;
    }

    public String buildUserPrompt(String initialQuestion, List<DrawnCard> drawnCards, List<ChatMessage> history, String userNewMessage) {
        StringBuilder sb = new StringBuilder();
        sb.append("BỐI CẢNH 3 LÁ BÀI TRÊN BÀN CHO CÂU HỎI BAN ĐẦU: \"").append(initialQuestion).append("\"\n");
        for (DrawnCard dc : drawnCards) {
            sb.append("- Vị trí [").append(dc.getPositionName()).append("]: Lá ")
              .append(dc.getCard().getNameVi()).append(" (")
              .append(dc.isReversed() ? "NGƯỢC" : "XUÔI").append(")\n");
        }

        if (history != null && !history.isEmpty()) {
            sb.append("\nLỊCH SỬ TRÒ CHUYỆN TRƯỚC ĐÓ:\n");
            for (ChatMessage msg : history) {
                sb.append(msg.getSender().name()).append(": ").append(msg.getContent()).append("\n");
            }
        }

        sb.append("\nNGƯỜI DÙNG VỪA HỎI THÊM: \"").append(userNewMessage).append("\"\n");
        sb.append("Hãy đưa ra câu trả lời ấm áp, thấu đáo và chữa lành.");
        return sb.toString();
    }
}