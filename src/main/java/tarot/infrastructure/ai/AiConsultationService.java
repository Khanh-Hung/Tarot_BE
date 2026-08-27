package tarot.infrastructure.ai;

import org.springframework.stereotype.Service;
import tarot.domain.entities.Card;
import tarot.domain.entities.ChatMessage;
import tarot.domain.entities.DrawnCard;
import tarot.domain.entities.User;
import tarot.domain.enums.SpreadType;
import tarot.domain.enums.Topic;

import java.util.List;

/**
 * Bộ não AI Luận Giải & Tham Vấn Tarot Chữa Lành:
 * 1. Sinh bản luận giải tổng quan ban đầu chuẩn Markdown.
 * 2. Trả lời các câu hỏi tâm sự/hỏi sâu tiếp theo dựa trên 3 lá bài trên bàn.
 */
@Service
public class AiConsultationService {

    /**
     * Sinh bản luận giải ban đầu chuẩn Markdown (Hỗ trợ Streaming / Markdown rendering)
     */
    public String generateInitialReading(User user, String userQuestion, Topic topic, SpreadType spreadType, List<DrawnCard> drawnCards) {
        String userName = (user != null && user.getUsername() != null) ? user.getUsername() : "Bạn";
        String zodiac = (user != null && user.getZodiacSign() != null) ? user.getZodiacSign().name() : "Chưa xác định";

        StringBuilder sb = new StringBuilder();
        sb.append("# 🔮 THÔNG ĐIỆP VŨ TRỤ DÀNH CHO ").append(userName.toUpperCase()).append("\n\n");
        sb.append("> **Chủ đề**: ").append(topic.name()).append(" | **Cung Hoàng Đạo**: ").append(zodiac).append("\n");
        sb.append("> **Câu hỏi**: *\"").append(userQuestion).append("\"*\n\n");

        sb.append("## 🌌 1. Tổng Quan Năng Lượng & Tâm Trạng\n");
        sb.append("Chào ").append(userName).append(", vũ trụ đã lắng nghe câu hỏi của bạn. ");
        sb.append("Năng lượng hiện tại cho thấy bạn đang đứng trước một giai đoạn nhiều suy tư và biến chuyển cảm xúc. ");
        sb.append("Các lá bài xuất hiện hôm nay mang đến một bức tranh toàn cảnh nhằm soi sáng nút thắt mà bạn đang trăn trở.\n\n");

        sb.append("## 🎴 2. Chi Tiết Các Lá Bài Đã Bốc\n\n");
        for (DrawnCard dc : drawnCards) {
            Card c = dc.getCard();
            String orientation = dc.isReversed() ? "🔄 NGƯỢC (Reversed)" : "✨ XUÔI (Upright)";
            String meaning = dc.isReversed() ? c.getReversedMeaning() : c.getUprightMeaning();

            sb.append("### 📍 ").append(dc.getPositionName()).append(": **").append(c.getNameVi())
              .append(" (").append(c.getNameEn()).append(")** - *").append(orientation).append("*\n");
            sb.append("- **Nguyên tố**: ").append(c.getElement()).append(" | **Từ khóa**: `").append(c.getKeywords()).append("`\n");
            sb.append("- **Thông điệp lá bài**: ").append(meaning).append("\n");
            sb.append("- **Luận giải trong hoàn cảnh của bạn**: ");
            if (dc.isReversed()) {
                sb.append("Lá bài ngược chỉ ra rằng có một lực cản tâm lý hoặc sự ngần ngại từ phía bạn. Hãy cẩn trọng để không bị cuốn vào những suy nghĩ tiêu cực hoặc sự tự ti vô cớ.\n\n");
            } else {
                sb.append("Nguồn năng lượng thuận dòng đang chảy mạnh. Đây là tín hiệu tích cực báo hiệu sự thấu hiểu, cơ hội mới và những chuyển biến sáng sủa sắp tới.\n\n");
            }
        }

        sb.append("## 💡 3. Lời Khuyên Hành Động Thực Tế\n");
        sb.append("1. **Giữ tâm thế tĩnh lặng**: Đừng vội vàng đưa ra quyết định hệ trọng trong lúc cảm xúc đang xáo trộn.\n");
        sb.append("2. **Lắng nghe trực giác**: Tin tưởng vào tiếng nói nội tâm, những chi tiết nhỏ đang mách bảo cho bạn con đường đúng đắn.\n");
        sb.append("3. **Chủ động tạo ra thay đổi**: Nắm quyền kiểm soát cuộc sống thay vì chờ đợi hoàn cảnh bên ngoài tác động.\n\n");

        sb.append("## ✨ 4. Câu Khẳng Định Chữa Lành (Affirmation)\n");
        sb.append("> 🌿 *\"Tôi xứng đáng có được sự bình an, niềm vui và những điều tốt đẹp nhất. Mọi thử thách xảy đến đều là bài học quý giá giúp tôi trưởng thành hơn.\"*\n\n");
        sb.append("---\n");
        sb.append("💬 *Nếu bạn muốn hỏi sâu hơn về bất kỳ lá bài nào, hãy nhắn tin ngay bên dưới để AI Reader giải đáp nhé!*");

        return sb.toString();
    }

    /**
     * Trả lời câu hỏi tiếp theo bám sát vào 3 lá bài đang nằm trên bàn
     */
    public String generateChatReply(String userQuestion, List<DrawnCard> drawnCards, List<ChatMessage> history, String userNewMessage) {
        StringBuilder cardContext = new StringBuilder();
        for (DrawnCard dc : drawnCards) {
            cardContext.append("- Vị trí [").append(dc.getPositionName()).append("]: Lá ")
                       .append(dc.getCard().getNameVi()).append(" (")
                       .append(dc.isReversed() ? "NGƯỢC" : "XUÔI").append(")\n");
        }

        return String.format(
            "Cảm ơn câu hỏi sâu sắc của bạn! Dựa trên các lá bài đang nằm trên bàn:\n%s\n" +
            "Về thắc mắc *\"%s\"* của bạn: Các lá bài phản ánh rằng nút thắt chủ yếu đến từ sự kỳ vọng và cách bạn nhìn nhận vấn đề. " +
            "Bạn hãy thử buông bỏ bớt áp lực kiểm soát kết quả, cho đối phương và chính mình một khoảng không gian thở. " +
            "Vũ trụ đang dần sắp xếp mọi thứ theo quỹ đạo tốt nhất cho bạn!",
            cardContext.toString(),
            userNewMessage
        );
    }
}