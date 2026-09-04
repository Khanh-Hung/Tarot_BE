package tarot.infrastructure.ai.classifiers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tarot.domain.enums.Topic;
import tarot.infrastructure.ai.core.AiModelClient;
import tarot.infrastructure.ai.core.TopicClassifier;
import tarot.infrastructure.ai.prompts.TopicClassifierPromptBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class LlmTopicClassifier implements TopicClassifier {

    private final AiModelClient aiModelClient;
    private final TopicClassifierPromptBuilder promptBuilder;

    @Override
    public Topic classify(String userQuestion) {
        if (userQuestion == null || userQuestion.isBlank()) {
            return Topic.GENERAL_GUIDANCE;
        }

        String sysPrompt = promptBuilder.buildSystemInstruction();
        String userPrompt = promptBuilder.buildUserPrompt(userQuestion);

        // Gọi trực tiếp AI LLM để phân loại ngữ nghĩa
        String aiRawOutput = aiModelClient.generateContent(sysPrompt, userPrompt);

        if (aiRawOutput != null && !aiRawOutput.isBlank()) {
            String cleaned = aiRawOutput.trim().toUpperCase();
            for (Topic t : Topic.values()) {
                if (cleaned.contains(t.name())) {
                    log.info("LLM Classifier successfully classified question into Topic: {}", t);
                    return t;
                }
            }
        }

        log.warn("Failed to classify question '{}' with AI, defaulting to GENERAL_GUIDANCE", userQuestion);
        return Topic.GENERAL_GUIDANCE;
    }
}