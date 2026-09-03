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

    private static final List<String> FALLBACK_QUESTIONS = List.of(
            "Lời khuyên vũ trụ dành cho công việc và sự nghiệp sắp tới?",
            "Mối quan hệ hiện tại đang cần tôi thấu hiểu điều gì?",
            "Năng lượng và cơ hội mới nào đang chờ đón tôi trong tháng này?",
            "Tôi nên buông bỏ điều gì để đón nhận bình an và thịnh vượng?",
            "Quyết định sắp tới của tôi có dẫn tới kết quả tích cực không?",
            "Nút thắt tâm lý nào đang cản trở bước tiến của tôi?",
            "Thông điệp chữa lành tâm hồn sâu sắc nhất lúc này là gì?"
    );

    public Result<List<String>> handle(GetSuggestedQuestionsQuery query) {
        String sysInstruction = """
            Bạn là Nyxoris Tarot Master - một chiêm tinh gia và bậc thầy Tarot thấu thị nội tâm.
            Nhiệm vụ: Hãy tạo ra ĐÚNG 3 câu hỏi bói bài Tarot mẫu bằng tiếng Việt thật tinh tế, ngắn gọn, sâu sắc và gợi mở.
            Mỗi câu hỏi từ 10 đến 25 từ, có dấu hỏi cuối câu.
            Chỉ trả về định dạng JSON Array chứa ĐÚNG 3 chuỗi câu hỏi (không thêm markdown hay bất kỳ chữ nào khác):
            ["Câu hỏi 1?", "Câu hỏi 2?", "Câu hỏi 3?"]
            """;

        String userPrompt = "Hãy gợi ý đúng 3 câu hỏi Tarot huyền thị sâu sắc "
                + (query.zodiac() != null && !query.zodiac().isBlank() && !query.zodiac().equals("UNKNOWN") ? "cho người thuộc cung hoàng đạo " + query.zodiac() : "cho người đang tìm định hướng")
                + (query.topic() != null && !query.topic().isBlank() ? " liên quan đến chủ đề " + query.topic() : "")
                + ".";

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
