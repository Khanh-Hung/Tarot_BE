package tarot.infrastructure.ai.core;

import tarot.domain.enums.Topic;

/**
 * Value Object: Kết quả trả về từ quá trình AI Consultation
 */
public record AiReadingResult(
    Topic detectedTopic,
    String markdownContent
) {}