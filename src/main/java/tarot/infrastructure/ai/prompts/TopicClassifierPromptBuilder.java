package tarot.infrastructure.ai.prompts;

import org.springframework.stereotype.Component;

@Component
public class TopicClassifierPromptBuilder {

    public String buildSystemInstruction() {
        return """
            Bạn là một bộ phân loại ngữ nghĩa NLP chuyên biệt cho Tarot & Chiêm tinh học.
            Nhiệm vụ: Phân loại câu hỏi của người dùng vào đúng DUY NHẤT 1 trong 4 enum sau:
            - LOVE_AND_RELATIONSHIP: Câu hỏi về tình yêu, crush, người cũ, hôn nhân, tình cảm, người yêu, chia tay, hẹn hò.
            - CAREER_AND_FINANCE: Câu hỏi về công việc, tiền bạc, thăng tiến, kinh doanh, đầu tư, lương thưởng, học tập, thi cử, sự nghiệp.
            - SELF_GROWTH_AND_HEALING: Câu hỏi về chữa lành, mệt mỏi, bế tắc, áp lực, lo âu, tâm thức, bình an nội tâm, cô đơn.
            - GENERAL_GUIDANCE: Câu hỏi tổng quan cuộc sống, vận mệnh tương lai, thông điệp ngày mới.
            
            QUY TẮC BẮT BUỘC: Chỉ trả về chính xác 1 từ đại diện cho enum (VD: LOVE_AND_RELATIONSHIP), không kèm giải thích hay văn bản thừa nào khác.
            """;
    }

    public String buildUserPrompt(String userQuestion) {
        return "CÂU HỎI: \"" + userQuestion + "\"\nENUM CHỦ ĐỀ:";
    }
}