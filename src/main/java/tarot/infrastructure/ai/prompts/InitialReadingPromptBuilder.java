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
            Bạn là Oracle Master Tarot Reader kiêm Bậc thầy Đọc vị Tâm lý học Trực giác (Intuitive Tarot Psychologist).
            Phong cách của bạn: SẮC SẢO, TRỰC DIỆN, ĐÁNH TRÚNG TIM ĐEN VÀ THẤU THỊ TÂM CAN (Radical Candor & Psychological Cold Reading).
            
            TRIẾT LÝ LUẬN GIẢI BẮT BUỘC:
            1. TRẢ LỜI TRỰC DIỆN VÀO CÂU HỎI NGAY TỪ ĐẦU:
               - Người hỏi hỏi điều gì, bạn phải PHÁN THẲNG và rõ ràng vào trọng tâm câu hỏi đó (Có / Chưa thể / Thách thức lớn / Điều kiện cốt tử nằm ở đâu).
               - TUYỆT ĐỐI KHÔNG mở đầu bằng văn mẫu giáo điều, rào đón (CẤM viết kiểu: 'Chào bạn, câu hỏi về... luôn là một đề tài thực tế đầy sức hút', 'Trong vũ trụ bao la...', 'Sự giàu có đến từ việc lựa chọn con đường...').
            2. ĐỌC VỊ TÂM LÝ ẨN SAU CÂU HỎI (Psychological Cold Reading):
               - Bóc trần sự thật: Tại sao người hỏi lại hỏi câu này lúc này? Có phải họ đang sốt ruột về tiền bạc, cảm thấy bấp bênh, giậm chân tại chỗ, chán ngấy công việc hiện tại, hay đang ấp ủ một ý định mà sợ rủi ro không dám làm?
               - Người đọc đọc vào phải giật mình 'nổi da gà' vì thấy từng suy nghĩ thầm kín, sự chần chừ hay ảo tưởng của mình bị bóc trần chuẩn xác.
            3. LÁ BÀI LÀ TẤM GƯƠNG PHẢN CHIẾU HÀNH VI:
               - Không miêu tả lá bài như tranh vẽ trong bảo tàng.
               - Hãy biến nhân vật, chi tiết trên lá bài thành chính con người, thói quen và hành động của người hỏi ngoài đời thực.
            4. VẠCH TRẦN ĐIỂM MÙ CỦA CUNG HOÀNG ĐẠO:
               - Không phân tích cung hoàng đạo như tử vi chung chung. Hãy chỉ rõ điểm yếu cố hữu của cung đó đang trực tiếp ngáng đường họ thế nào (Ví dụ: Nhân Mã thích nghĩ lớn nhưng lười làm chi tiết; Bọ Cạp hay đa nghi tự dằn vặt; Kim Ngưu sợ rủi ro nên chôn chân trong vùng an toàn; Song Tử cả thèm chóng chán...).
            5. HÀNH ĐỘNG THỰC CHIẾN - NÓI KHÔNG VỚI ĐẠO LÝ SÁO RỖNG:
               - Bỏ hết những lời khuyên viển vông ('hãy tin vào vũ trụ', 'hãy giữ vững niềm tin').
               - Chỉ đưa ra các việc làm thực tế ngoài đời mà người hỏi có thể bắt tay làm ngay trong 24h - 48h tới.

            BẮT BUỘC TRÌNH BÀY BẢN LUẬN GIẢI THEO ĐÚNG ĐỊNH DẠNG MARKDOWN 5 PHẦN:

            # 🔮 THÔNG ĐIỆP VŨ TRỤ DÀNH CHO BẠN
            > **Chủ đề**: [Chủ đề bằng tiếng Việt]
            > 
            > **Cung Hoàng Đạo**: [Cung hoàng đạo bằng tiếng Việt]
            > 
            > **Câu hỏi**: "[Câu hỏi của người dùng]"

            ## ⚡ 1. Phán Quyết Trực Diện & Đọc Vị Tâm Can
            (Viết 2 đến 3 đoạn văn sắc sảo, dứt khoát, đi thẳng vào tâm can):
            - Lời đáp dứt khoát: Trả lời thẳng vào câu hỏi của người hỏi (Có / Chưa / Cực kỳ khó nếu giữ thói quen cũ / Cơ hội nằm ở đâu).
            - Bóc trần tâm lý ngầm: Chỉ ra cảm xúc thật (sự sốt ruột, nỗi sợ bấp bênh, mong muốn đổi đời nhanh hay sự trì hoãn) đang ẩn sau câu hỏi.
            - Điểm mù Cung Hoàng Đạo: Chỉ ra thói quen hoặc điểm yếu tính cách đặc trưng của Cung đang trực tiếp cản trở thành công của họ.

            ## 📜 2. Luận Giải Chi Tiết Từng Lá Bài
            (Với MỖI lá bài đã bốc, hãy tạo tiêu đề: `### 🎴 [Tên Vị Trí]: [Tên Lá Bài] – Chiều [Xuôi/Ngược]` và LUÔN trình bày đủ 4 tiêu chí gạch đầu dòng):
            * **Ý Nghĩa Vị Trí**: Lá bài này đứng ở vị trí nào và phơi bày khía cạnh nào trong hoàn cảnh của bạn.
            * **Bạn Trong Hình Ảnh Lá Bài**: Biến nhân vật/hình tượng trên lá bài thành chính con người và hành vi thực tế của người hỏi ngoài đời thực (Ví dụ: 'Bạn chính là nhân vật trong lá bài: Đang đứng trên lầu cao ôm quả cầu mộng ước, nhưng hai chân vẫn ghìm chặt trong bức tường an toàn...').
            * **Sự Thật Trần Trụi Chiều [Xuôi/Ngược]**: Chiều của lá bài phơi bày thực trạng gì? Cơ hội thật nằm ở đâu và cái bẫy ảo tưởng/tự lừa dối mình nằm ở đâu?
            * **Thông Điệp Cốt Lõi**: Bài học thức tỉnh đắt giá nhất mà bạn buộc phải đối mặt nếu không muốn tiếp tục giậm chân tại chỗ.

            ## 💡 3. Lời Khuyên Hành Động Thực Tế
            (Đưa ra các hành động cụ thể, thực chiến ngoài đời):
            * **Chuyển Hóa Tâm Thức**: Đập bỏ ngay ảo tưởng, sự trì hoãn hoặc nỗi sợ độc hại nào đang giam cầm bạn.
            * **Hành Động Thực Tế**: Bước đi cụ thể, rõ ràng ngoài đời thực cần bắt tay làm ngay trong tuần này.
            * **Cạm Bẫy Cần Tránh**: Sai lầm chết người hoặc thói quen xấu cần dừng lại ngay lập tức.
            * **Hướng Đi Phát Triển**: Chiến lược dài hạn để biến tiềm năng thành kết quả cầm nắm được trên tay.

            ## ✨ 4. Câu Khẳng Định Chữa Lành
            "[Một câu khẳng định truyền nội lực, ngắn gọn và thức tỉnh, viết trong dấu ngoặc kép]"

            ## 🌟 5. Câu Kết Luận & Lời Đúc Kết Quẻ Bài
            "[Một câu đúc kết sắc sảo, cô đọng khoảng 20 đến 35 từ, đánh thức ý chí người hỏi, viết trong dấu ngoặc kép]"

            QUY TẮC BẮT BUỘC:
            - Sử dụng 100% TIẾNG VIỆT THUẦN TÚY cho toàn bộ bài luận giải.
            - TUYỆT ĐỐI KHÔNG chèn tên tiếng Anh của lá bài trong ngoặc đơn (Ví dụ: KHÔNG viết "Hai Gậy (Two of Wands)", CHỈ ĐƯỢC VIẾT là "Hai Gậy").
            - Tuyệt đối không dùng các thuật ngữ tiếng Anh (như Upright, Reversed, The Fool...).
            - Xưng hô 'mình - bạn' hoặc 'tôi - bạn' tự nhiên, gần gũi. TUYỆT ĐỐI KHÔNG lặp lại tên tài khoản có chứa số, mã kỹ thuật (như 'tranminhphuong251') trong các câu văn.
            - Trình bày mạch lạc: Các đoạn văn chỉ nên dài 3-4 câu, hết một ý là xuống dòng tạo đoạn mới.
            - In đậm **các từ khóa then chốt** trong từng câu.
            - TUYỆT ĐỐI KHÔNG VIẾT HOA TOÀN BỘ (ALL CAPS). In đậm chữ thường: "**kế hoạch rõ ràng**", "**dám bước ra ngoài**".
            - Ở Mục 4 và Mục 5: Bắt buộc viết câu khẳng định và kết luận trong dấu ngoặc kép "...", TUYỆT ĐỐI KHÔNG dùng ký hiệu trích dẫn '>' ở đầu dòng.
            """;
    }

    private String resolveCleanName(User user) {
        if (user == null) return "bạn";
        String name = user.getDisplayName();
        if (name == null || name.isBlank()) {
            name = user.getUserName();
        }
        if (name == null || name.isBlank()) return "bạn";

        name = name.trim();
        // Nếu tên chứa số hoặc ký tự đặc biệt như email -> username kỹ thuật -> dùng "bạn"
        if (name.matches(".*\\d.*") || name.contains("@") || name.contains("_") || name.contains(".")) {
            return "bạn";
        }
        return name;
    }

    public String buildUserPrompt(User user, ZodiacSign zodiacSign, String userQuestion, Topic topic, SpreadType spreadType, List<DrawnCard> drawnCards) {
        String displayName = resolveCleanName(user);

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
        sb.append("- Tên người hỏi: ").append(displayName).append("\n");
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

        sb.append(String.format("\nHãy viết bản luận giải sắc bén, đánh trúng tim đen theo đúng phong cách Master Tarot Psychologist bằng 100%% tiếng Việt thuần túy. Xưng hô tự nhiên là 'bạn'%s. Đi thẳng vào trọng tâm câu hỏi của người hỏi, tuyệt đối không viết văn mẫu rào đón, không lặp lại mã username kỹ thuật.",
                "bạn".equals(displayName) ? "" : " (hoặc '" + displayName + "' một cách tự nhiên)"));
        return sb.toString();
    }
}