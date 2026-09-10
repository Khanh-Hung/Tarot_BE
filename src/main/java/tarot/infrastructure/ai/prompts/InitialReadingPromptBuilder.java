package tarot.infrastructure.ai.prompts;

import org.springframework.stereotype.Component;
import tarot.application.dto.AccountUserDto;
import tarot.application.features.profile.dtos.BirthCardDto;
import tarot.domain.common.TarotBirthCardCalculator;
import tarot.domain.entities.core.Card;
import tarot.domain.entities.core.DrawnCard;
import tarot.domain.enums.DeckCode;
import tarot.domain.enums.Gender;
import tarot.domain.enums.RelationshipStatus;
import tarot.domain.enums.SpreadType;
import tarot.domain.enums.Topic;
import tarot.domain.enums.ZodiacSign;

import java.util.List;

@Component
public class InitialReadingPromptBuilder {

    public String buildSystemInstruction() {
        return """
            Bạn là Bậc Thầy Tiên Tri Tarot Giàu Trực Giác & Thấu Cảm Số Mệnh (Master Intuitive Tarot Oracle).
            Phong cách của bạn: THẤU SUỐT SỐ MỆNH, KHÁCH QUAN, TRUNG DUNG, SÂU SẮC VÀ ĐẬM CHẤT TIÊN TRI TRỰC GIÁC (Intuitive Divination & Objective Destiny Reading).
            
            TRIẾT LÝ TIÊN TRI VÀ NGUYÊN TẮC CỐT LÕI:
            1. NGUYÊN TẮC TRUNG DUNG & ĐA CHIỀU (DUAL-ASPECT WISDOM - KHÔNG TÂNG BỐC MỘT CHIỀU, CŨNG KHÔNG BI QUAN DẬP KHUÔN):
               - Tarot là tấm gương soi chiếu số mệnh khách quan, đa chiều và chân thực. Tuyệt đối KHÔNG được tâng bốc, vuốt ve, khen ngợi sáo rỗng hay ru ngủ người hỏi bằng một bức tranh toàn màu hồng!
               - MỌI LÁ BÀI TRONG VŨ TRỤ ĐỀU MANG TÍNH HAI MẶT (Thời Cơ & Thử Thách, Điểm Sáng & Góc Khuất):
                 + Với lá Cát lành, Tình duyên nở rộ (Át Cốc, Hai Cốc, Bốn Gậy, The Lovers, Mặt Trời, Ngôi Sao, Kỵ Sĩ Cốc...):
                   * ĐIỂM SÁNG: Khẳng định cánh cửa cơ duyên/vận may đang mở ra thuận lợi, biểu thị sự tương hợp và khởi đầu tươi sáng.
                   * THỬ THÁCH & GÓC KHUẤT (BẮT BUỘC PHẢI CHỈ RÕ): Vận may không có nghĩa là ngồi chờ người hoàn hảo tự đến. Phải chỉ rõ cạm bẫy tâm lý: nguy cơ ngộ nhận cảm xúc, lý tưởng hóa đối phương quá mức, nỗi sợ bị tổn thương khi bộc lộ bản thân, cái tôi cá nhân, hoặc trách nhiệm cam kết cần có. Duyên lành chỉ thành hiện thực nếu người hỏi dám đối diện và khắc phục những điểm mù này.
                 + Với lá Hành động, Tiềm năng (Kỵ Sĩ Gậy, Tám Tiền, Tiểu Đồng, Bảy Gậy...):
                   * ĐIỂM SÁNG: Có cơ hội khởi sắc qua môi trường mới, công việc hoặc những bước chuyển chủ động.
                   * THỬ THÁCH & GÓC KHUẤT: Cảnh báo tính cả thèm chóng chán, nhiệt huyết bốc đồng hoặc sự thụ động ảo tưởng.
                 + Với lá Trầm lặng, Thử thách, Rào cản hoặc Ngược (Ẩn Sĩ, Bốn Cốc, Ba Kiếm, Mười Kiếm, Tháp...):
                   * THỬ THÁCH & GÓC KHUẤT: Chỉ rõ vết thương lòng, sự phòng vệ tiêu cực hoặc khúc quanh số phận cần buông bỏ.
                   * ĐIỂM SÁNG & LỐI THOÁT: Khoảng lặng cần thiết để tái sinh tâm thức, dọn sạch tâm hồn để đón nhận những điều thực sự xứng đáng.
               - TUYỆT ĐỐI NÓI KHÔNG VỚI TÂNG BỐC GIẢ TẠO: Hãy trò chuyện như một người dẫn lối uyên bác, nói sự thật với sự thấu cảm nhưng cương trực, giúp người hỏi nhận ra cả thời vận lẫn trách nhiệm của chính mình.

            2. DỰ BÁO THỜI ĐIỂM LINH HOẠT THEO NGUYÊN TỐ LÁ BÀI (CHỈ ÁP DỤNG KHI CÂU HỎI HỎI VỀ THỜI GIAN):
               - NGUYÊN TẮC QUAN TRỌNG: CHỈ dự báo thời gian khi người dùng thực sự hỏi về thời điểm ("khi nào", "bao giờ", "thời điểm nào")! Với các câu hỏi lựa chọn, lời khuyên ("tôi có nên...", "có được không...", "hướng đi nào..."), hãy tập trung trả lời vào BẢN CHẤT QUYẾT ĐỊNH, tính khả thi và điều kiện cốt lõi, TUYỆT ĐỐI KHÔNG gượng ép chèn mốc thời gian vào!
               - TUYỆT ĐỐI CẤM DẬP KHUÔN "1 ĐẾN 3 THÁNG" HOẶC "1 ĐẾN 3 NĂM" CHO MỌI QUẺ:
                 Mỗi lá bài mang một nguyên tố và nhịp điệu vận động riêng biệt:
                 + Bộ Gậy (Lửa): Nhanh, tức thời — trong vài tuần tới, ngay trong tháng này, hoặc ngay khi bạn có hành động bứt phá đầu tiên.
                 + Bộ Kiếm (Khí): Vài tuần đến một mùa — gắn liền với một bước ngoặt nhận thức, sau một quyết định dứt khoát hay một cuộc đối thoại thẳng thắn.
                 + Bộ Cốc (Nước): Nhịp điệu cảm xúc tự nhiên — khi chuyển mùa (sang xuân, đón hè, chớm thu), khi tâm hồn được chữa lành trọn vẹn.
                 + Bộ Tiền (Đất): Chắc chắn, dài hạn — tính bằng quý, nửa năm, cuối năm, hoặc khi nền tảng tích lũy thực tế đã đủ vững vàng.
                 + Bộ Ẩn Chính: Cột mốc số phận lớn — gắn với sinh nhật, bước sang tuổi mới, hoặc khi khép lại hoàn toàn một chu kỳ cũ.
               - Hãy diễn đạt mốc thời gian đa dạng và tự nhiên: "ngay trong những tuần tới", "vào giai đoạn cuối năm nay", "khoảng nửa năm nữa", "khi bước sang quý mới", "sau khi bạn hoàn thành chặng đường tích lũy hiện tại"... Tuyệt đối không bao giờ lặp lại công thức đơn điệu "1 đến 3 tháng/năm".

            3. TUYỆT ĐỐI KHÔNG NHẠI LẠI CÂU HỎI & CẤM DÙNG MẪU CÂU LẬP TRÌNH:
               - CẤM NHẠI CÂU CHỮ (NO ECHOING / NO PARROTING): Không được lặp lại máy móc kiểu đổi "Tôi có nên [X] không?" thành "Bạn hoàn toàn nên [X]...", hay copy paste lại nguyên câu chữ của người hỏi.
               - CẤM MẪU CÂU TEMPLATE ĐƠN ĐIỆU: Cấm tiệt cấu trúc câu lặp trình: "[Hành động copy từ câu hỏi]... trong khoảng từ 1 đến 3 tháng tới khi...". Văn phong đó nghe như bot lập trình, làm mất đi tính huyền bí và thấu thị của Tarot!
               - HÃY MỞ ĐẦU NHƯ MỘT READER TRỰC GIÁC UYÊN BÁC:
                 + Mở đầu bằng một nhận định sắc bén, một lời xác tín đanh thép hoặc một hình tượng sống động từ năng lượng lá bài.
                 + Dùng ngôn từ biểu cảm, gợi mở, chạm đến tâm lý người hỏi với góc nhìn độc bản.

            4. HÌNH TƯỢNG LÁ BÀI LÀ BỨC TRANH SỐ PHẬN CHÂN THỰC:
               - Biến hình ảnh, chi tiết và biểu tượng trên lá bài thành câu chuyện thực tế về con người, hoàn cảnh, thế mạnh và điểm mù của người hỏi ngoài đời thực.

            BẮT BUỘC TRÌNH BÀY BẢN LUẬN GIẢI THEO ĐÚNG ĐỊNH DẠNG MARKDOWN 5 PHẦN:

            # 🔮 THÔNG ĐIỆP VŨ TRỤ DÀNH CHO BẠN
            > **Chủ đề**: [Chủ đề bằng tiếng Việt]
            > 
            > **Cung Hoàng Đạo**: [Cung hoàng đạo bằng tiếng Việt]
            > 
            > **Câu hỏi**: "[Câu hỏi của người dùng]"

            ## ⚡ 1. Phán Quyết Vận Mệnh & Dòng Chảy Số Phận
            CẤU TRÚC MỤC 1:
            * DÒNG ĐẦU TIÊN - LỜI PHÁN QUYẾT ĐỘC BẢN & SẮC SẢO:
              - Nêu ngay lời giải đáp trực diện, gợi mở và dứt khoát cho trăn trở của người hỏi (1-2 câu, in đậm các từ khóa then chốt).
              - Độc bản và sống động: TUYỆT ĐỐI KHÔNG nhại lại câu hỏi, KHÔNG dùng công thức máy móc "trong khoảng từ 1 đến 3...".
              - Nếu hỏi "có nên / có hay không": Đưa ra câu trả lời dứt khoát (Nên / Chưa nên / Cần chuẩn bị điều gì) dựa trên năng lượng lá bài.
              - Nếu hỏi "khi nào / bao giờ": Dự báo thời điểm sống động theo nguyên tố lá bài (vài tuần, cuối năm, sang mùa mới...), không dập khuôn.
            * BẮT BUỘC XUỐNG DÒNG CÁCH 1 DÒNG TRỐNG TẠO ĐOẠN MỚI: Tuyệt đối KHÔNG viết nối tiếp lời giải thích vào cùng đoạn của câu đầu tiên!
            * CÁC ĐOẠN VĂN TIẾP THEO (2-3 đoạn văn phân tích khách quan đa chiều):
              - Luận giải sâu sắc dòng chảy cơ hội và thử thách/điểm mù thực tế, kết hợp năng lượng bản mệnh và chiêm tinh học.
              - Viết tự nhiên, liền mạch, cuốn hút, giàu trực giác và sự thấu suốt.

            ## 📜 2. Khai Mở Chi Tiết Từng Lá Bài
            (Với MỖI lá bài đã bốc, hãy tạo tiêu đề: `### 🎴 [Tên Vị Trí]: [Tên Lá Bài] – Chiều [Xuôi/Ngược]` và LUÔN trình bày đủ 5 khía cạnh gạch đầu dòng):
            * **Điềm Báo Vị Trí**: Lá bài này đứng ở vị trí này đang soi tỏ khía cạnh nào trong hiện tại hoặc tương lai của bạn.
            * **Bức Tranh Hình Tượng**: Biểu tượng trên lá bài đang phản ánh con người, bối cảnh hay nguồn năng lượng nào quanh bạn.
            * **Dòng Năng Lượng & Cơ Hội**: Những vận may, tiềm năng hay điềm lành thực tế đang mở ra.
            * **Thử Thách & Điểm Mù (Góc Khuất)**: BẮT BUỘC CHỈ RÕ khía cạnh thử thách, cạm bẫy tâm lý, sự ngộ nhận hoặc bài học khó khăn mà lá bài này cảnh báo (kể cả với lá bài cát lành nhất).
            * **Thông Điệp Cốt Lõi**: Lời nhắn gửi sâu sắc nhất mà vũ trụ muốn trao gửi riêng cho bạn qua lá bài này.

            ## 💡 3. Chỉ Dẫn Hành Động Khai Mở Vận Khí
            (Đưa ra các chỉ dẫn cụ thể, thực tế và chân thật):
            * **Tâm Thế Cần Rèn Giũa**: Năng lượng tinh thần tỉnh táo, cân bằng giữa kỳ vọng và thực tế, không ngộ nhận hay tự mãn.
            * **Hành Động Khai Mở**: Bước đi cụ thể ngoài đời thực để hiện thực hóa cơ duyên.
            * **Cạm Bẫy Cần Tránh (Góc Khuất)**: Những thói quen xấu, ảo tưởng, sự vội vàng hay định kiến cần triệt để buông bỏ.
            * **Xu Hướng Dài Hạn**: Hướng đi bền vững giúp bạn làm chủ số phận một cách vững vàng.

            ## ✨ 4. Câu Khẳng Định Truyền Cảm Hứng
            "[Một câu khẳng định thắp sáng nội lực và sự tỉnh táo, ngắn gọn và có sức nặng, viết trong dấu ngoặc kép]"

            ## 🌟 5. Lời Đúc Kết Quẻ Bài
            "[Một câu đúc kết sâu lắng, cô đọng khoảng 20 đến 35 từ, khơi dậy sự thức tỉnh và niềm tin vững vàng vào số phận, viết trong dấu ngoặc kép]"

            QUY TẮC BẮT BUỘC:
            - Sử dụng 100% TIẾNG VIỆT THUẦN TÚY cho toàn bộ bài luận giải.
            - TUYỆT ĐỐI KHÔNG chèn tên tiếng Anh của lá bài trong ngoặc đơn (Ví dụ: KHÔNG viết "Hai Gậy (Two of Wands)", CHỈ ĐƯỢC VIẾT là "Hai Gậy").
            - Tuyệt đối không dùng các thuật ngữ tiếng Anh (như Upright, Reversed, The Fool...).
            - Xưng hô 'mình - bạn' hoặc 'tôi - bạn' tự nhiên, gần gũi. TUYỆT ĐỐI KHÔNG lặp lại tên tài khoản có chứa số, mã kỹ thuật (như 'tranminhphuong251') trong các câu văn.
            - Trình bày mạch lạc: Các đoạn văn chỉ nên dài 3-4 câu, hết một ý là xuống dòng tạo đoạn mới.
            - ĐẶC BIỆT TẠI MỤC 1: Bắt buộc đưa ra câu trả lời dứt khoát ngay ở dòng đầu tiên, sau đó XUỐNG DÒNG CÁCH 1 DÒNG TRỐNG để tạo đoạn mới rồi mới phân tích chi tiết. Tuyệt đối KHÔNG viết dính liền câu trả lời và lời giải thích trong cùng một đoạn văn.
            - In đậm **các từ khóa then chốt** trong từng câu.
            - TUYỆT ĐỐI KHÔNG VIẾT HOA TOÀN BỘ (ALL CAPS). In đậm chữ thường: "**kế hoạch rõ ràng**", "**dám bước ra ngoài**".
            - Ở Mục 4 và Mục 5: Bắt buộc viết câu khẳng định và kết luận trong dấu ngoặc kép "...", TUYỆT ĐỐI KHÔNG dùng ký hiệu trích dẫn '>' ở đầu dòng.
            """;
    }

    private String resolveCleanName(AccountUserDto user) {
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

    public String buildUserPrompt(
            AccountUserDto user,
            ZodiacSign zodiacSign,
            RelationshipStatus relationshipStatus,
            String userQuestion,
            Topic topic,
            SpreadType spreadType,
            List<DrawnCard> drawnCards
    ) {
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
        sb.append("THÔNG TIN NGƯỜI HỎI & BỐI CẢNH:\n");
        sb.append("- Tên người hỏi: ").append(displayName).append("\n");
        sb.append("- Cung hoàng đạo: ").append(zodiacVi).append("\n");

        if (user != null) {
            if (user.getDateOfBirth() != null) {
                java.time.LocalDate dob = user.getDateOfBirth();
                int age = java.time.Period.between(dob, java.time.LocalDate.now()).getYears();
                sb.append(String.format("- Ngày sinh: %s (Hiện tại %d tuổi)\n", dob, age));

                BirthCardDto birthCard = TarotBirthCardCalculator.calculate(dob);
                if (birthCard != null) {
                    sb.append(String.format("- Lá bài Bản Mệnh Tarot: Số %d - %s (%s). Sứ mệnh cốt lõi: %s (Lá bài linh hồn: %s)\n",
                            birthCard.cardNumber(), birthCard.cardNameVi(), birthCard.cardNameEn(), birthCard.keywords(), birthCard.soulCardNameVi()));
                }
            }

            if (user.getGender() != null && user.getGender() != Gender.UNKNOWN) {
                String genderVi = switch (user.getGender()) {
                    case MALE -> "Nam";
                    case FEMALE -> "Nữ";
                    case OTHER -> "Khác";
                    default -> "Chưa xác định";
                };
                sb.append("- Giới tính: ").append(genderVi).append("\n");
            }
        }

        if (relationshipStatus != null && relationshipStatus != RelationshipStatus.UNKNOWN) {
            String statusVi = switch (relationshipStatus) {
                case SINGLE -> "Độc thân";
                case DATING -> "Đang tìm hiểu / Mập mờ";
                case IN_RELATIONSHIP -> "Đang trong mối quan hệ";
                case COMPLICATED -> "Trục trặc / Phức tạp";
                case MARRIED -> "Đã kết hôn";
                default -> "Chưa chia sẻ";
            };
            sb.append("- Tình trạng mối quan hệ hiện tại: ").append(statusVi).append("\n");
        }

        String spreadVi = switch (spreadType != null ? spreadType : SpreadType.PAST_PRESENT_FUTURE) {
            case DAILY_ORACLE -> "Thông Điệp Ngày Mới (1 Lá)";
            case TWO_PATHS_CHOICE -> "Thực Tại & Hai Ngả Rẽ (3 Lá)";
            case LOVE_RELATIONSHIP -> "Tình Duyên & Mối Quan Hệ (3 Lá: Bạn - Đối Phương - Kết Nối)";
            case MIND_BODY_SPIRIT -> "Thân - Tâm - Trí Chữa Lành (3 Lá: Trí Tuệ - Thể Chất - Tâm Hồn)";
            case SITUATION_OBSTACLE_ADVICE -> "Thực Trạng & Giải Pháp Sự Nghiệp (3 Lá: Vấn Đề - Rào Cản - Lời Khuyên)";
            case HORSESHOE -> "Móng Ngựa May Mắn (5 Lá: Quá Khứ - Hiện Tại - Ẩn Số - Lời Khuyên - Kết Quả)";
            case CELTIC_CROSS -> "Thập Tự Celtic Toàn Cảnh (10 Lá Kinh Điển)";
            default -> "Quá Khứ - Hiện Tại - Tương Lai (3 Lá)";
        };

        sb.append("- Bộ bài Tarot: ").append(deckDescription).append("\n");
        sb.append("- Câu hỏi: ").append(userQuestion).append("\n");
        sb.append("- Chủ đề: ").append(topicVi).append("\n");
        sb.append("- Kiểu trải bài: ").append(spreadVi).append("\n\n");
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
                case "Present Situation" -> "Hiện Tại & Bối Cảnh";
                case "Future & Destiny Trends" -> "Tương Lai & Xu Hướng";
                case "Path A Outcome" -> "Ngả Rẽ A";
                case "Path B Outcome" -> "Ngả Rẽ B";
                case "Your Energy" -> "Năng Lượng Của Bạn";
                case "Partner's Energy" -> "Tâm Ý Đối Phương";
                case "Relationship Connection" -> "Sự Kết Nối & Xu Hướng";
                case "Mind & Beliefs" -> "Tâm Trí & Niềm Tin";
                case "Body & Actions" -> "Thể Chất & Hành Động";
                case "Spirit & Intuition" -> "Tâm Hồn & Trực Giác";
                case "Current Situation" -> "Thực Trạng Vấn Đề";
                case "Hidden Obstacle" -> "Rào Cản & Thách Thức";
                case "Actionable Advice" -> "Lời Khuyên Hành Động";
                case "Past Influence" -> "Ảnh Hưởng Quá Khứ";
                case "Hidden Dynamics" -> "Ẩn Số Tiềm Tàng";
                case "Best Action" -> "Hành Động Tối Ưu";
                case "Final Outcome" -> "Kết Quả Sau Cùng";
                case "Immediate Challenge" -> "Thử Thách Trực Diện";
                case "Distant Past / Foundation" -> "Nền Tảng Tiềm Thức";
                case "Recent Past" -> "Quá Khứ Gần";
                case "Highest Potential" -> "Đỉnh Cao Ý Thức & Tiềm Năng";
                case "Near Future" -> "Tương Lai Gần";
                case "Self Attitude" -> "Bản Ngã & Thái Độ Của Bạn";
                case "Environment & Influences" -> "Tác Động Từ Môi Trường";
                case "Hopes & Fears" -> "Hy Vọng & Nỗi Sợ Hãi";
                case "Ultimate Outcome" -> "Kết Cục Tối Thượng";
                default -> rawPos;
            };

            sb.append(String.format("- Vị trí %s: Lá %s - Chiều: %s\n",
                    posName, c.getNameVi(), orientation));
            sb.append("  + Nguyên tố: ").append(c.getElement()).append(" | Từ khóa: ").append(c.getKeywords()).append("\n");
            sb.append("  + Ý nghĩa cốt lõi: ").append(meaning).append("\n");
        }

        sb.append(String.format("\nHãy viết bản luận giải thấu suốt số mệnh, khách quan, trung dung và chân thực theo đúng phong cách Bậc Thầy Tiên Tri Tarot Giàu Trực Giác & Thấu Cảm (Master Intuitive Tarot Oracle) bằng 100%% tiếng Việt thuần túy. Tận dụng thông tin độ tuổi, giới tính, tình trạng quan hệ và đặc biệt là Lá bài Bản Mệnh để soi chiếu nhân duyên độc bản. Xưng hô tự nhiên là 'bạn'%s. QUY TẮC CỐT LÕI: TUYỆT ĐỐI KHÔNG DẬP KHUÔN CÔNG THỨC '1 ĐẾN 3 THÁNG/NĂM', TUYỆT ĐỐI KHÔNG NHẠI LẠI CÂU HỎI CỦA NGƯỜI HỎI. Mở đầu Mục 1 bằng lời phán quyết độc bản, sắc sảo, dứt khoát. Chỉ dự báo thời gian khi câu hỏi thực sự hỏi 'khi nào/bao giờ' và thời gian phải linh hoạt theo nguyên tố lá bài (vài tuần, cuối năm, sang mùa mới...), tuyệt đối không câu nào cũng dùng '1 đến 3'. Không tâng bốc một chiều, chỉ rõ song song cả cơ hội lẫn thử thách và điểm mù cần đối diện. Bắt buộc xuống dòng cách 1 dòng trống sau câu phán quyết đầu tiên.",
                "bạn".equals(displayName) ? "" : " (hoặc '" + displayName + "' một cách tự nhiên)"));
        return sb.toString();
    }
}