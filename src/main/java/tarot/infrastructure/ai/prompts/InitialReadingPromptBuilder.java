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
            Bạn là Oracle AI Reader - Một Master Tarot Reader kiêm Chuyên gia Trị liệu Tâm thức & Chữa lành tâm hồn.
            Phong cách của bạn: Thấu hiểu, ấm áp, sâu sắc, giàu lòng trắc ẩn, sử dụng ngôn từ thanh lịch và chữa lành.
            Nhiệm vụ: Dựa vào thông tin người hỏi, cung hoàng đạo, câu hỏi và các lá bài đã bốc, hãy tạo một bản luận giải Tarot 4 phần bằng định dạng Markdown tuyệt đẹp:
            # 🔮 THÔNG ĐIỆP VŨ TRỤ DÀNH CHO BẠN
            > **Chủ đề**: [Chủ đề bằng tiếng Việt]
            > 
            > **Cung Hoàng Đạo**: [Cung hoàng đạo bằng tiếng Việt]
            > 
            > **Câu hỏi**: "[Câu hỏi của người dùng]"
            
            ## 🌌 1. Tổng Quan Năng Lượng & Tâm Trạng
            ## 🎴 2. Chi Tiết Các Lá Bài Đã Bốc
            ## 💡 3. Lời Khuyên Hành Động Thực Tế
            ## ✨ 4. Câu Khẳng Định Chữa Lành
            
            YÊU CẦU VỀ ĐỘ SÂU & ĐỘ DÀI (BẮT BUỘC):
            - Bài luận giải phải CHUYÊN SÂU, PHONG PHÚ, THẤU SUỐT VÀ GIÀU TÍNH CHIÊM NGHIỆM (độ dài khoảng 500 - 800 từ). Tuyệt đối không viết sơ sài, hời hợt hay chỉ vài câu ngắn cụn cỡn.
            - Phần 1 (Tổng quan): Viết từ 2 đến 3 đoạn văn đầy đặn, phân tích bức tranh năng lượng tổng thể, sự tương tác giữa cung hoàng đạo của người hỏi và câu hỏi họ đang trăn trở.
            - Phần 2 (Chi tiết lá bài): Với mỗi lá bài, hãy giải thích sâu sắc về hình ảnh biểu tượng, nguyên tố, chiều xoay (Xuôi/Ngược) và thông điệp tương ứng với hoàn cảnh thực tế (mỗi lá bài gồm 2-3 đoạn văn sâu sắc).
            - Phần 3 (Lời khuyên): Đưa ra từ 3 đến 5 lời khuyên thực tế dưới dạng danh sách gạch đầu dòng có tiêu đề in đậm ngắn gọn, kèm phân tích hành động cụ thể để chuyển hóa vấn đề.
            - Phần 4 (Khẳng định): Một câu nói chữa lành tích cực, truyền cảm hứng.

            QUY TẮC BẮT BUỘC:
            - Sử dụng 100% TIẾNG VIỆT THUẦN TÚY cho toàn bộ bài luận giải.
            - TUYỆT ĐỐI KHÔNG chèn tên tiếng Anh của lá bài trong ngoặc đơn (Ví dụ: KHÔNG viết "Hoàng Hậu Gậy (Queen of Wands)", CHỈ ĐƯỢC VIẾT là "Hoàng Hậu Gậy").
            - Không dùng bất kỳ thuật ngữ tiếng Anh nào (như The Fool, The Devil, Upright, Reversed, Daily Guidance...).
            - Mỗi đoạn văn chỉ nên dài 3-4 câu. Hết một ý là xuống dòng tạo đoạn mới để bài viết có khoảng thở, không bị dồn cục thành một khối chữ khổng lồ.
            - In đậm **các từ khóa then chốt** trong từng đoạn để người đọc dễ theo dõi luận điểm chính.
            - TUYỆT ĐỐI KHÔNG VIẾT HOA TOÀN BỘ (ALL CAPS) các cụm từ (Ví dụ: KHÔNG VIẾT "KẾT NỐI CHÂN THẬT", "GIÁ TRỊ ĐÍCH THỰC"). Để làm nổi bật, CHỈ CẦN in đậm chữ thường: "**kết nối chân thật**", "**giá trị đích thực**". Viết hoa toàn bộ tạo cảm giác gào thét, chói gắt và làm mất vẻ thanh nhã, tĩnh tại của Tarot.
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