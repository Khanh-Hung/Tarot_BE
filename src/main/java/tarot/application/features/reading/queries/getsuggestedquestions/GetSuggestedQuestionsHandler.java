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
            "Lời khuyên vũ trụ dành cho công việc và sự nghiệp sắp tới của tôi?",
            "Mối quan hệ hiện tại đang cần tôi thấu hiểu và hoàn thiện điều gì?",
            "Năng lượng và cơ hội mới nào đang chờ đón tôi trong thời gian này?",
            "Tôi nên buông bỏ điều gì để đón nhận bình an và thịnh vượng?",
            "Quyết định sắp tới của tôi có dẫn tới kết quả tích cực không?",
            "Nút thắt tâm lý nào đang cản trở bước tiến của tôi?",
            "Thông điệp chữa lành tâm hồn sâu sắc nhất lúc này dành cho tôi là gì?",
            "Người ấy đang có cảm xúc và suy nghĩ gì về mối liên kết giữa hai người?",
            "Lộ trình tài chính nào giúp tôi đạt được sự tự chủ và vững vàng?",
            "Làm thế nào để tôi cân bằng giữa công việc bận rộn và bình yên nội tại?",
            "Bài học lớn nhất mà giai đoạn này đang dạy cho tôi là gì?",
            "Tôi nên chuẩn bị tinh thần ra sao trước bước ngoặt mới trong cuộc sống?",
            "Xu hướng tình cảm của tôi trong thời gian tới sẽ biến chuyển thế nào?",
            "Có ngả rẽ tiềm năng nào mà tôi chưa nhận ra hay chưa dám dấn bước?",
            "Tôi cần làm gì để vượt qua cảm giác mông lung và tìm lại đam mê?",
            "Làm sao để tôi giải tỏa những lo âu vô cớ và tìm lại sự tự tin vốn có?",
            "Tôi nên lắng nghe trực giác hay lý trí trong tình huống hiện tại?"
    );

    public Result<List<String>> handle(GetSuggestedQuestionsQuery query) {
        String sysInstruction = """
            Bạn là Nyxoris Tarot Master - một chiêm tinh gia và bậc thầy Tarot thấu thị nội tâm.
            Nhiệm vụ: Hãy tạo ra ĐÚNG 3 câu hỏi bói bài Tarot mẫu bằng tiếng Việt thuần túy, thật tinh tế, ngắn gọn, sâu sắc và gợi mở.
            Quy tắc bắt buộc:
            - Câu hỏi phải đóng vai người hỏi xưng "tôi" (Ví dụ: "Tôi nên làm gì để...", "Năng lượng nào đang hỗ trợ tôi...", "Làm thế nào để tôi...").
            - TUYỆT ĐỐI KHÔNG dùng tên tiếng Anh của cung hoàng đạo (như Gemini, Aries, Leo, Pisces...).
            - TUYỆT ĐỐI KHÔNG gọi người hỏi là "Gemini" hay bất kỳ tên cung nào.
            - Mỗi câu hỏi từ 10 đến 25 từ, có dấu hỏi cuối câu.
            - Chỉ trả về định dạng JSON Array chứa ĐÚNG 3 chuỗi câu hỏi (không thêm markdown hay chữ thừa):
            ["Câu hỏi 1?", "Câu hỏi 2?", "Câu hỏi 3?"]
            """;

        String zodiacVi = (query.zodiac() != null && !query.zodiac().isBlank() && !query.zodiac().equals("UNKNOWN"))
                ? ZODIAC_VI.getOrDefault(query.zodiac().toUpperCase(), query.zodiac())
                : null;

        String userPrompt = "Hãy gợi ý đúng 3 câu hỏi Tarot huyền thị sâu sắc "
                + (zodiacVi != null ? "cho người thuộc cung " + zodiacVi : "cho người đang tìm định hướng")
                + (query.topic() != null && !query.topic().isBlank() ? " liên quan đến chủ đề " + query.topic() : "")
                + ". Các câu hỏi phải ở ngôi thứ nhất (xưng 'tôi'), tuyệt đối không dùng từ tiếng Anh.";

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
            log.warn("Không thể parse gợi ý câu hỏi từ Gemini AI, sử dụng fallback: {}", e.getMessage());
        }

        var shuffled = new ArrayList<>(FALLBACK_QUESTIONS);
        Collections.shuffle(shuffled);
        return Result.success(shuffled.subList(0, Math.min(3, shuffled.size())));
    }
}
