package tarot.infrastructure.ai.prompts;

import org.springframework.stereotype.Component;
import tarot.domain.entities.Card;
import tarot.domain.entities.DrawnCard;
import tarot.domain.entities.User;
import tarot.domain.enums.DeckCode;
import tarot.domain.enums.SpreadType;
import tarot.domain.enums.Topic;
import tarot.domain.enums.ZodiacSign;

import java.util.List;

@Component
public class InitialReadingPromptBuilder {

    public String buildSystemInstruction() {
        return """
            Bạn là Oracle AI Reader - Một Master Tarot Reader kiêm Chuyên gia Trị liệu Tâm thức & Chữa lành tâm hồn.
            Phong cách của bạn: Thấu hiểu, ấm áp, sâu sắc, giàu lòng trắc ẩn, sử dụng ngôn từ thanh lịch và chữa lành.
            Nhiệm vụ: Dựa vào thông tin người hỏi, cung hoàng đạo, câu hỏi và các lá bài đã bốc, hãy tạo một bản luận giải Tarot 4 phần bằng định dạng Markdown tuyệt đẹp:
            # 🔮 THÔNG ĐIỆP VŨ TRỤ DÀNH CHO [TÊN NGƯỜI DÙNG]
            > **Chủ đề**: [Chủ đề] | **Cung Hoàng Đạo**: [Cung]
            > **Câu hỏi**: "[Câu hỏi]"
            
            ## 🌌 1. Tổng Quan Năng Lượng & Tâm Trạng
            ## 🎴 2. Chi Tiết Các Lá Bài Đã Bốc
            ## 💡 3. Lời Khuyên Hành Động Thực Tế
            ## ✨ 4. Câu Khẳng Định Chữa Lành (Affirmation)
            """;
    }

    public String buildUserPrompt(User user, ZodiacSign zodiacSign, String userQuestion, Topic topic, SpreadType spreadType, List<DrawnCard> drawnCards) {
        String userName = (user != null && user.getUsername() != null) ? user.getUsername() : "Bạn";
        String zodiac = (zodiacSign != null) ? zodiacSign.name() : "Chưa xác định";

        DeckCode deck = (drawnCards != null && !drawnCards.isEmpty() && drawnCards.get(0).getCard() != null)
                ? drawnCards.get(0).getCard().getDeckCode()
                : DeckCode.RIDER_WAITE_CLASSIC;

        String deckDescription = switch (deck) {
            case THOTH_ALEISTER -> "Thoth Tarot (Aleister Crowley 1944 - Trường phái Huyền học Hermetic, Chiêm Tinh & Giả Kim Thuật)";
            case MARSEILLE_HERMETIC -> "Tarot de Marseille (Thế kỷ 17 - Trường phái Cổ điển Pháp thời Phục Hưng, Trực giác nguyên bản)";
            default -> "Rider-Waite-Smith 1909 (Trường phái Biểu tượng học Kinh điển & Tâm lý học thường nhật)";
        };

        StringBuilder sb = new StringBuilder();
        sb.append("THÔNG TIN QUẺ BÓI:\n");
        sb.append("- Người hỏi: ").append(userName).append("\n");
        sb.append("- Cung hoàng đạo: ").append(zodiac).append("\n");
        sb.append("- Bộ bài Tarot: ").append(deckDescription).append("\n");
        sb.append("- Câu hỏi: ").append(userQuestion).append("\n");
        sb.append("- Chủ đề: ").append(topic.name()).append("\n");
        sb.append("- Kiểu trải bài: ").append(spreadType.name()).append("\n\n");
        sb.append("DANH SÁCH LÁ BÀI ĐÃ BỐC:\n");

        for (DrawnCard dc : drawnCards) {
            Card c = dc.getCard();
            String orientation = dc.isReversed() ? "NGƯỢC (Reversed)" : "XUÔI (Upright)";
            String meaning = dc.isReversed() ? c.getReversedMeaning() : c.getUprightMeaning();

            sb.append(String.format("- Vị trí [%s]: Lá %s (%s) - Chiều: %s\n",
                    dc.getPositionName(), c.getNameVi(), c.getNameEn(), orientation));
            sb.append("  + Nguyên tố: ").append(c.getElement()).append(" | Từ khóa: ").append(c.getKeywords()).append("\n");
            sb.append("  + Ý nghĩa cốt lõi: ").append(meaning).append("\n");
        }

        sb.append("\nHãy viết bản luận giải chuyên sâu, mang năng lượng chữa lành bằng tiếng Việt.");
        return sb.toString();
    }
}