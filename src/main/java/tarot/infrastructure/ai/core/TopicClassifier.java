package tarot.infrastructure.ai.core;

import tarot.domain.enums.Topic;

/**
 * Strategy Interface: Bộ phân loại chủ đề câu hỏi
 */
public interface TopicClassifier {
    Topic classify(String userQuestion);
}