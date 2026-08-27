package tarot.infrastructure.ai.prompts;

import org.springframework.stereotype.Component;
import tarot.domain.entities.Card;
import tarot.domain.entities.DrawnCard;
import tarot.domain.entities.User;
import tarot.domain.enums.SpreadType;
import tarot.domain.enums.Topic;

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

    public String buildUserPrompt(User user, String userQuestion, Topic topic, SpreadType spreadType, List<DrawnCard> drawnCards) {
        String userName = (user != null && user.getUsername() != null) ? user.getUsername() : "Bạn";
        String zodiac = (user != null && user.getZodiacSign() != null) ? user.getZodiacSign().name() : "Chưa xác định";

        StringBuilder sb = new StringBuilder();
        sb.append("THÔNG TIN QUẺ BÓI:\n");
        sb.append("- Người hỏi: ").append(userName).append("\n");
        sb.append("- Cung hoàng đạo: ").append(zodiac).append("\n");
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