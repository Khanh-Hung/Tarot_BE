package tarot.infrastructure.ai.prompts;

import org.springframework.stereotype.Component;
import tarot.domain.entities.core.ChatMessage;
import tarot.domain.entities.core.DrawnCard;

import java.util.List;

@Component
public class ChatConsultationPromptBuilder {

    public String buildSystemInstruction() {
        return """
            Bạn là người giải bài Tarot thông minh, tinh tế và sâu sắc. Bạn đang trong cuộc trò chuyện tiếp nối với người dùng về quẻ bài vừa bốc.
            
            QUY TẮC BẮT BUỘC:
            1. PHẠM VI TRẢ LỜI & CÂU HỎI NGOÀI LỀ:
               - Vai trò duy nhất của bạn tại đây là giải đáp quẻ bài Tarot này.
               - Nếu người dùng hỏi các câu hỏi hoàn toàn KHÔNG liên quan đến Tarot hay quẻ bài (như giải toán, viết code, dịch văn bản, thời sự, v.v.):
                 + KHÔNG giải quyết các yêu cầu ngoài lề đó.
                 + KHÔNG trả lời dài dòng, không văn vở triết lý hay lặp lại các từ ngữ sáo rỗng ('kết nối vũ trụ', 'toàn bộ tâm trí', 'bạn đã sẵn sàng chưa').
                 + Hãy từ chối một cách THÔNG MINH, NGẮN GỌN (tối đa 1-2 câu), ví dụ: "Mình chỉ ở đây để đồng hành và làm rõ quẻ bài Tarot này cùng bạn thôi. Bạn có câu hỏi nào về lá bài hay hoàn cảnh của mình thì cứ nhắn mình nhé!"
            
            2. TẬP TRUNG ĐÚNG LÁ BÀI & CÂU HỎI ĐƯỢC HỎI:
               - Khi người dùng hỏi về một lá bài cụ thể hoặc một khía cạnh cụ thể: CHỈ tập trung giải thích đúng lá bài đó hoặc đúng vấn đề người dùng hỏi.
               - TUYỆT ĐỐI KHÔNG giải thích lại tất cả các lá bài khác trên bàn nếu người dùng không hỏi đến. Không liệt kê dàn trải gây lan man, thừa thãi.
            
            3. NGÔN NGỮ & XƯNG HÔ:
               - DÙNG 100% TIẾNG VIỆT THUẦN TÚY. Tuyệt đối KHÔNG dùng tiếng Anh (trừ khi người dùng hỏi bằng tiếng Anh).
               - Xưng hô 'mình - bạn' tự nhiên, thân thiện. Tuyệt đối KHÔNG tự xưng 'Oracle AI Reader', 'AI Reader' hay robot.
            
            4. HÌNH THỨC & PHONG THÁI:
               - Đi thẳng vào trọng tâm, trả lời cô đọng, sâu sắc và hữu ích.
               - CẤM các câu kết thúc sáo rỗng lặp đi lặp lại như: "Bạn đã sẵn sàng cùng mình khám phá chưa?". Hãy đối thoại tự nhiên, chân thật như người với người.
            """;
    }

    private String translatePosition(String rawPos) {
        if (rawPos == null) return "";
        return switch (rawPos) {
            case "Daily Guidance" -> "Thông Điệp Ngày Mới";
            case "Current Reality" -> "Thực Tại Hiện Nhiên";
            case "Past & Foundations" -> "Quá Khứ & Nền Tảng";
            case "Present Situation" -> "Hiện Tại & Thử Thách";
            case "Future & Destiny Trends" -> "Tương Lai & Xu Hướng";
            case "Path A Outcome" -> "Ngả Rẽ A";
            case "Path B Outcome" -> "Ngả Rẽ B";
            default -> rawPos;
        };
    }

    public String buildUserPrompt(String initialQuestion, List<DrawnCard> drawnCards, List<ChatMessage> history, String userNewMessage) {
        StringBuilder sb = new StringBuilder();
        sb.append("BỐI CẢNH CÁC LÁ BÀI TRÊN BÀN CHO CÂU HỎI BAN ĐẦU: \"").append(initialQuestion).append("\"\n");
        for (DrawnCard dc : drawnCards) {
            String posName = translatePosition(dc.getPositionName());
            sb.append("- Vị trí: ").append(posName).append(" - Lá bài: ")
              .append(dc.getCard().getNameVi()).append(" (")
              .append(dc.isReversed() ? "Chiều Ngược" : "Chiều Xuôi").append(")\n");
        }

        if (history != null && !history.isEmpty()) {
            sb.append("\nLỊCH SỬ TRÒ CHUYỆN:\n");
            for (ChatMessage msg : history) {
                sb.append(msg.getSender().name()).append(": ").append(msg.getContent()).append("\n");
            }
        }

        sb.append("\nNGƯỜI DÙNG HỎI: \"").append(userNewMessage).append("\"\n");
        sb.append("YÊU CẦU: Trả lời chân thành, tự nhiên, bám sát các lá bài. DÙNG 100% TIẾNG VIỆT, KHÔNG CHÈN BẤT KỲ TỪ TIẾNG ANH NÀO.");
        return sb.toString();
    }
}