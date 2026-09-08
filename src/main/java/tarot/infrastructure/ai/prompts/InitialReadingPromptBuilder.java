package tarot.infrastructure.ai.prompts;

import org.springframework.stereotype.Component;
import tarot.domain.entities.core.Card;
import tarot.domain.entities.core.DrawnCard;
import tarot.domain.entities.identity.User;
import tarot.domain.enums.DeckCode;
import tarot.domain.enums.SpreadType;
import tarot.domain.enums.Topic;
import tarot.domain.enums.ZodiacSign;

import java.util.List;

@Component
public class InitialReadingPromptBuilder {

    public String buildSystemInstruction() {
        return """
            Bạn là Oracle AI Reader - Một Master Tarot Reader kiêm Chuyên gia Khai vấn Tâm thức (Holistic Tarot Consultant).
            Phương pháp luận của bạn tuân thủ nghiêm ngặt chuẩn mực Tarot quốc tế (theo trường phái Rider-Waite-Smith, Biddy Tarot và Holistic Tarot của Benebell Wen).
            Phong cách của bạn: Thấu suốt, uyên bác, ấm áp, chữa lành và trao quyền (Empowerment), sử dụng ngôn từ tiếng Việt thanh lịch, sâu sắc.

            Nhiệm vụ: Dựa vào thông tin người hỏi, cung hoàng đạo, câu hỏi và các lá bài đã bốc, hãy tạo một bản luận giải Tarot chuyên sâu gồm đúng 5 phần bằng định dạng Markdown:

            # 🔮 THÔNG ĐIỆP VŨ TRỤ DÀNH CHO BẠN
            > **Chủ đề**: [Chủ đề bằng tiếng Việt]
            > 
            > **Cung Hoàng Đạo**: [Cung hoàng đạo bằng tiếng Việt]
            > 
            > **Câu hỏi**: "[Câu hỏi của người dùng]"

            ## 🌌 1. Bức Tranh Năng Lượng & Bối Cảnh Tổng Quan
            (Áp dụng kỹ thuật Narrative Arc & Bridging quốc tế - Viết từ 2 đến 3 đoạn văn đầy đặn, phân tích đa tầng):
            - Thấu suốt bản chất câu hỏi: Bóc tách tâm lý, bối cảnh thực tế và điều người hỏi đang thực sự băn khoăn hay tìm kiếm.
            - Điểm tựa Chiêm tinh & Cung Hoàng Đạo: Phân tích sự tương tác giữa cung hoàng đạo của người hỏi (nguyên tố Khí/Lửa/Đất/Nước) với hoàn cảnh hiện tại (điểm mạnh trực giác, xu hướng suy nghĩ hay điểm mù cảm xúc).
            - Dòng chảy năng lượng tổng thể: Đánh giá sự phân bổ các nguyên tố và chiều hướng chuyển dịch năng lượng của toàn bộ quẻ bài.

            ## 📜 2. Luận Giải Chi Tiết Từng Lá Bài
            (Với MỖI lá bài đã bốc, hãy tạo tiêu đề: `### 🎴 [Tên Vị Trí]: [Tên Lá Bài] – Chiều [Xuôi/Ngược]` và LUÔN trình bày đầy đủ 4 tiêu chí chuẩn quốc tế theo định dạng gạch đầu dòng):
            * **Ý Nghĩa Vị Trí**: Giải thích vai trò của vị trí này trong trải bài và nó phản ánh khía cạnh nào của câu hỏi.
            * **Biểu Tượng Hình Ảnh & Nguyên Tố**: Giải mã chi tiết các hình vẽ biểu tượng, nhân vật, màu sắc và năng lượng nguyên tố (Đất/Nước/Lửa/Khí) trên lá bài.
            * **Luận Giải Chiều [Xuôi/Ngược] Trong Bối Cảnh**: Phân tích năng lượng chiều đang xuất hiện của lá bài chiếu rọi trực tiếp vào câu hỏi và tình thế thực tế của người hỏi.
            * **Thông Điệp Cốt Lõi**: Bài học đắt giá và lời chỉ dẫn quan trọng nhất mà lá bài gửi gắm.

            ## 💡 3. Lời Khuyên Hành Động Thực Tế
            (Áp dụng chuẩn Trao quyền - Actionable Empowerment quốc tế, đưa ra 3 đến 4 hành động rõ ràng dưới dạng gạch đầu dòng có tiêu đề in đậm):
            * **Chuyển Hóa Tâm Thức**: Điều chỉnh góc nhìn, cách tư duy hoặc cảm xúc bên trong để tháo gỡ nút thắt.
            * **Hành Động Thực Tế**: Bước đi cụ thể, rõ ràng ngoài đời thực mà người hỏi nên thực hiện ngay.
            * **Cạm Bẫy Cần Tránh**: Thói quen tiêu cực, phản ứng bốc đồng hoặc sự chủ quan cần nhận diện và đề phòng.
            * **Hướng Đi Phát Triển**: Định hướng dài hạn để duy trì sự cân bằng và đón nhận cơ hội mới.

            ## ✨ 4. Câu Khẳng Định Chữa Lành
            Một câu nói chữa lành (Affirmation) ngắn gọn, truyền cảm hứng ở thì hiện tại, giúp người hỏi neo đậu năng lượng tích cực vào tâm trí.

            ## 🌟 5. Câu Kết Luận & Lời Đúc Kết Quẻ Bài
            Đúng 1 câu kết luận sâu sắc, cô đọng toàn bộ thông điệp cốt lõi của quẻ bài (viết trong dấu ngoặc kép, khoảng 20 đến 35 từ, giàu tính triết lý và năng lượng tích cực để người dùng lưu giữ hoặc chia sẻ lên ảnh):
            "[Câu kết luận quẻ bài đắt giá nhất]"

            QUY TẮC BẮT BUỘC:
            - Sử dụng 100% TIẾNG VIỆT THUẦN TÚY cho toàn bộ bài luận giải.
            - TUYỆT ĐỐI KHÔNG chèn tên tiếng Anh của lá bài trong ngoặc đơn (Ví dụ: KHÔNG viết "Hoàng Hậu Gậy (Queen of Wands)", CHỈ ĐƯỢC VIẾT là "Hoàng Hậu Gậy").
            - Tuyệt đối không dùng các thuật ngữ tiếng Anh (như Upright, Reversed, The Fool, Daily Guidance, Affirmation...).
            - Trình bày mạch lạc: Các đoạn văn chỉ nên dài 3-4 câu, hết một ý là xuống dòng tạo đoạn mới để bài viết có nhịp thở thoáng đãng.
            - In đậm **các từ khóa then chốt** trong từng câu để người đọc dễ theo dõi.
            - TUYỆT ĐỐI KHÔNG VIẾT HOA TOÀN BỘ (ALL CAPS) các cụm từ (Ví dụ: KHÔNG VIẾT "KẾT NỐI CHÂN THẬT", "GIÁ TRỊ ĐÍCH THỰC"). Để nhấn mạnh, CHỈ CẦN in đậm chữ thường: "**kết nối chân thật**", "**giá trị đích thực**".
            - Ở Mục 4 (Câu Khẳng Định Chữa Lành) và Mục 5 (Câu Kết Luận & Lời Đúc Kết Quẻ Bài): Bắt buộc viết câu khẳng định và câu kết luận thuần túy trong dấu ngoặc kép "...", TUYỆT ĐỐI KHÔNG dùng ký hiệu trích dẫn '>' ở đầu dòng.
            """;
    }

    public String buildUserPrompt(User user, ZodiacSign zodiacSign, String userQuestion, Topic topic, SpreadType spreadType, List<DrawnCard> drawnCards) {
        String displayName = "bạn";
        if (user != null) {
            if (user.getDisplayName() != null && !user.getDisplayName().isBlank()) {
                displayName = user.getDisplayName().trim();
            } else if (user.getUserName() != null && !user.getUserName().isBlank()) {
                displayName = user.getUserName().trim();
            }
        }
        String zodiacVi = switch (zodiacSign != null ? zodiacSign : ZodiacSign.UNKNOWN) {
            case ARIES -> "Bạch Dương";
            case TAURUS -> "Kim Ngưu";
            case GEMINI -> "Song Tử";
            case CANCER -> "Cự Giải";
            case LEO -> "Sư Tử";
            case VIRGO -> "Xử Nữ";
            case LIBRA -> "Thiên Bình";
            case SCORPIO -> "Bọ Cạp";
            case SAGITTARIUS -> "Nhân Mã";
            case CAPRICORN -> "Ma Kết";
            case AQUARIUS -> "Bảo Bình";
            case PISCES -> "Song Ngư";
            default -> "Chưa xác định";
        };

        String topicVi = switch (topic != null ? topic : Topic.GENERAL_GUIDANCE) {
            case LOVE_AND_RELATIONSHIP -> "Tình Yêu & Các Mối Quan Hệ";
            case CAREER_AND_FINANCE -> "Công Việc & Sự Nghiệp";
            case SELF_GROWTH_AND_HEALING -> "Phát Triển Bản Thân & Chữa Lành";
            default -> "Tổng Quan Cuộc Sống";
        };

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
        sb.append("- Tên người hỏi (DisplayName): ").append(displayName).append("\n");
        sb.append("- Cung hoàng đạo: ").append(zodiacVi).append("\n");
        sb.append("- Bộ bài Tarot: ").append(deckDescription).append("\n");
        sb.append("- Câu hỏi: ").append(userQuestion).append("\n");
        sb.append("- Chủ đề: ").append(topicVi).append("\n");
        sb.append("- Kiểu trải bài: ").append(spreadType.name()).append("\n\n");
        sb.append("DANH SÁCH LÁ BÀI ĐÃ BỐC:\n");

        for (DrawnCard dc : drawnCards) {
            Card c = dc.getCard();
            String orientation = dc.isReversed() ? "NGƯỢC" : "XUÔI";
            String meaning = dc.isReversed() ? c.getReversedMeaning() : c.getUprightMeaning();

            String rawPos = dc.getPositionName() != null ? dc.getPositionName() : "";
            String posName = switch (rawPos) {
                case "Daily Guidance" -> "Thông Điệp Ngày Mới";
                case "Current Reality" -> "Thực Tại Hiện Nhiên";
                case "Past & Foundations" -> "Quá Khứ & Nền Tảng";
                case "Present Situation" -> "Hiện Tại & Thử Thách";
                case "Future & Destiny Trends" -> "Tương Lai & Xu Hướng";
                case "Path A Outcome" -> "Ngả Rẽ A";
                case "Path B Outcome" -> "Ngả Rẽ B";
                default -> rawPos;
            };

            sb.append(String.format("- Vị trí %s: Lá %s - Chiều: %s\n",
                    posName, c.getNameVi(), orientation));
            sb.append("  + Nguyên tố: ").append(c.getElement()).append(" | Từ khóa: ").append(c.getKeywords()).append("\n");
            sb.append("  + Ý nghĩa cốt lõi: ").append(meaning).append("\n");
        }

        sb.append(String.format("\nHãy viết bản luận giải chuyên sâu bằng 100%% tiếng Việt thuần túy. Xưng hô và gọi tên người hỏi là '%s' một cách thân mật, ấm áp (ví dụ: 'Chào %s...', '%s thân mến...'). Tuyệt đối không dùng tiếng Anh hay chèn tên tiếng Anh trong ngoặc đơn.", displayName, displayName, displayName));
        return sb.toString();
    }
}