package tarot.application.features.reading.queries.getsuggestedquestions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tarot.application.common.result.Result;
import tarot.infrastructure.ai.core.AiModelClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetSuggestedQuestionsHandler {

    private final AiModelClient aiModelClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final java.util.Map<String, String> ZODIAC_VI = java.util.Map.ofEntries(
            java.util.Map.entry("ARIES", "Bạch Dương"),
            java.util.Map.entry("TAURUS", "Kim Ngưu"),
            java.util.Map.entry("GEMINI", "Song Tử"),
            java.util.Map.entry("CANCER", "Cự Giải"),
            java.util.Map.entry("LEO", "Sư Tử"),
            java.util.Map.entry("VIRGO", "Xử Nữ"),
            java.util.Map.entry("LIBRA", "Thiên Bình"),
            java.util.Map.entry("SCORPIO", "Bọ Cạp"),
            java.util.Map.entry("SAGITTARIUS", "Nhân Mã"),
            java.util.Map.entry("CAPRICORN", "Ma Kết"),
            java.util.Map.entry("AQUARIUS", "Bảo Bình"),
            java.util.Map.entry("PISCES", "Song Ngư")
    );

    private static final List<String> FALLBACK_QUESTIONS = List.of(
            "Công việc hiện tại của tôi sắp tới có cơ hội thăng tiến hay tăng lương không?",
            "Tôi có nên chuyển việc hoặc tìm hướng đi mới vào thời điểm này không?",
            "Người ấy có thực sự nghiêm túc và có tình cảm thật lòng với tôi không?",
            "Mối quan hệ hiện tại giữa hai chúng tôi có tương lai đi đường dài không?",
            "Tài chính và thu nhập của tôi trong vài tháng tới sẽ biến chuyển thế nào?",
            "Tôi có nên đầu tư hoặc góp vốn làm ăn trong giai đoạn này không?",
            "Tôi đang phân vân giữa hai lựa chọn, hướng đi nào sẽ mang lại kết quả tốt hơn cho tôi?",
            "Tôi nên làm gì để giải tỏa áp lực công việc và lấy lại động lực phát triển?",
            "Người yêu cũ có còn nghĩ về tôi không và tôi có nên liên lạc lại?",
            "Dự án hoặc kế hoạch kinh doanh sắp tới của tôi có gặp trở ngại gì không?",
            "Tôi cần thay đổi điều gì ở bản thân để công việc và tình duyên suôn sẻ hơn?",
            "Mối quan hệ này tôi nên tiếp tục kiên nhẫn hay đã đến lúc buông tay?",
            "Làm thế nào để tôi cải thiện tài chính và quản lý chi tiêu hiệu quả hơn?",
            "Sắp tới tôi có gặp được quý nhân hoặc cơ hội hợp tác nào đáng giá không?",
            "Bao giờ tôi mới gặp được người thực sự phù hợp để bắt đầu một mối quan hệ?"
    );

    private static final List<String> REALISTIC_TOPICS = List.of(
            "công việc và sự nghiệp (cơ hội thăng tiến, chuyển việc, áp lực công sở, định hướng phát triển)",
            "tình cảm và mối quan hệ (người ấy có thật lòng không, tương lai đi đường dài, có nên mở lời trước, buông tay hay tiếp tục)",
            "tài chính và tiền bạc (thu nhập sắp tới, cơ hội đầu tư, chi tiêu, kinh doanh)",
            "lựa chọn thực tế khi phân vân (nên ở lại hay rời đi, nên chọn phương án A hay B, nên bắt đầu hay chờ đợi)",
            "tâm lý và bản thân (giải tỏa áp lực, cải thiện tâm trạng, tìm lại động lực, điều cần chú ý tuần này)"
    );

    private static final java.util.Map<String, String> SPREAD_DESC = java.util.Map.of(
            "DAILY_ORACLE", "lời khuyên và điều cần lưu ý trong ngày",
            "PAST_PRESENT_FUTURE", "diễn biến vấn đề từ quá khứ, hiện tại đến tương lai",
            "TWO_PATHS_CHOICE", "so sánh giữa 2 lựa chọn khi đang phân vân"
    );

    public Result<List<String>> handle(GetSuggestedQuestionsQuery query) {
        String sysInstruction = """
            Bạn là trợ lý gợi ý câu hỏi Tarot thực tế cho người dùng đời thường.
            Nhiệm vụ: Hãy tạo ra ĐÚNG 3 câu hỏi Tarot ngắn gọn, thực tế, gần gũi với đời sống hằng ngày.

            Quy tắc BẮT BUỘC:
            1. TÍNH THỰC TẾ & ĐỜI THƯỜNG: Câu hỏi phải nói về những trăn trở THỰC SỰ của một người bình thường (công việc, thăng tiến, chuyển việc, người ấy có thật lòng không, tương lai tình cảm, tài chính tiền bạc, phân vân lựa chọn phương án A hay B).
            2. TUYỆT ĐỐI CẤM VĂN PHONG SẾN SẨM & VIỂN VÔNG: CẤM các từ ngữ hoa mỹ, tiểu thuyết diễm tình hay trừu tượng viển vông như: "tiếng gọi thầm kín", "nhịp đập xa lạ", "miền kỷ niệm cũ", "khát khao sục sôi", "năng lượng ẩn giấu", "vũ trụ đang gửi gắm", "ngã ba đường định mệnh", "ngã rẽ tâm hồn".
            3. Ngôi thứ nhất: Câu hỏi xưng "tôi" (Ví dụ: "Công việc hiện tại của tôi có cơ hội thăng tiến không?", "Người ấy có thực sự nghiêm túc với tôi không?").
            4. Độ dài: Mỗi câu từ 10 đến 20 từ, kết thúc bằng dấu hỏi chấm (?).
            5. Không dùng tiếng Anh: Tuyệt đối không dùng tên tiếng Anh của cung hoàng đạo (Gemini, Aries...).
            6. Định dạng trả về: Chỉ trả về JSON Array thuần túy chứa đúng 3 chuỗi câu hỏi (không markdown, không giải thích):
            ["Câu hỏi 1?", "Câu hỏi 2?", "Câu hỏi 3?"]
            """;

        String zodiacVi = (query.zodiac() != null && !query.zodiac().isBlank() && !query.zodiac().equals("UNKNOWN"))
                ? ZODIAC_VI.getOrDefault(query.zodiac().toUpperCase(), query.zodiac())
                : null;

        String randomTopic = REALISTIC_TOPICS.get(
                java.util.concurrent.ThreadLocalRandom.current().nextInt(REALISTIC_TOPICS.size())
        );
        String spreadContext = (query.topic() != null && !query.topic().isBlank())
                ? SPREAD_DESC.getOrDefault(query.topic().toUpperCase(), query.topic())
                : null;

        String userPrompt = "Hãy gợi ý đúng 3 câu hỏi Tarot cụ thể, thực tế và đời thường xoay quanh "
                + randomTopic
                + (spreadContext != null ? " (phù hợp với kiểu trải bài: " + spreadContext + ")" : "")
                + (zodiacVi != null ? " dành cho người thuộc cung " + zodiacVi : "")
                + ". Yêu cầu câu hỏi chân thực, đánh trúng tâm lý, không sáo rỗng hay viển vông.";

        try {
            String rawJson = aiModelClient.generateContent(sysInstruction, userPrompt);
            if (rawJson != null && !rawJson.isBlank()) {
                String cleanJson = rawJson.replace("```json", "").replace("```", "").trim();
                List<String> list = objectMapper.readValue(cleanJson, new TypeReference<List<String>>() {});
                if (list != null && !list.isEmpty()) {
                    return Result.success(list.subList(0, Math.min(3, list.size())));
                }
            }
        } catch (Exception e) {
            log.warn("Failed to parse suggested questions from Gemini AI, using fallback: {}", e.getMessage());
        }

        var shuffled = new ArrayList<>(FALLBACK_QUESTIONS);
        Collections.shuffle(shuffled);
        return Result.success(shuffled.subList(0, Math.min(3, shuffled.size())));
    }
}
