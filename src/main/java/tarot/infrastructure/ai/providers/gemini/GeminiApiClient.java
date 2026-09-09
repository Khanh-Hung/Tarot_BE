package tarot.infrastructure.ai.providers.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tarot.infrastructure.ai.core.AiModelClient;

import java.util.List;

/**
 * Client kết nối trực tiếp Google Gemini API qua RestClient và Typed Record DTOs
 */
@Slf4j
@Component
public class GeminiApiClient implements AiModelClient {

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public GeminiApiClient(
            @Value("${gemini.api-key:}") String apiKey,
            @Value("${gemini.model:gemini-2.5-flash}") String model
    ) {
        this.apiKey = apiKey;
        this.model = model;
        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta/models")
                .build();
    }

    @Override
    public String generateContent(String systemInstruction, String userPrompt) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("GEMINI_API_KEY is not configured! Please provide the API key in application.yml or environment variables.");
        }

        // Danh sách các model hoạt động theo thứ tự ưu tiên (Gemini 2.x & 3.x)
        List<String> modelsToTry = List.of(
            model,
            "gemini-flash-lite-latest",
            "gemini-3.5-flash",
            "gemini-3.6-flash",
            "gemini-2.5-flash"
        ).stream().filter(m -> m != null && !m.isBlank()).distinct().toList();

        Exception lastException = null;

        for (String currentModel : modelsToTry) {
            try {
                var request = GeminiRequest.of(systemInstruction, userPrompt);
                String uri = String.format("/%s:generateContent?key=%s", currentModel, apiKey);

                var response = restClient.post()
                        .uri(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(request)
                        .retrieve()
                        .body(GeminiResponse.class);

                String text = (response != null) ? response.extractText() : null;
                if (text != null && !text.isBlank()) {
                    return text;
                }
            } catch (Exception e) {
                lastException = e;
                log.warn("Model {} call failed ({}), attempting fallback model...", currentModel, e.getMessage());
            }
        }

        log.error("All Gemini models failed: {}", lastException != null ? lastException.getMessage() : "Unknown");
        throw new RuntimeException("Failed to connect to Google Gemini AI: " + (lastException != null ? lastException.getMessage() : "All models unavailable"), lastException);
    }

    // =========================================================================
    // --- TYPED RECORD DTOs (Ánh xạ chuẩn cấu trúc JSON Google Gemini API) ---
    // =========================================================================

    public record GeminiRequest(
            Instruction system_instruction,
            List<Content> contents,
            @com.fasterxml.jackson.annotation.JsonProperty("generationConfig")
            GenerationConfig generationConfig
    ) {
        public static GeminiRequest of(String systemPrompt, String userPrompt) {
            return new GeminiRequest(
                    new Instruction(List.of(new Part(systemPrompt))),
                    List.of(new Content("user", List.of(new Part(userPrompt)))),
                    new GenerationConfig(0.85, 0.95)
            );
        }
    }

    public record GenerationConfig(
            Double temperature,
            Double topP
    ) {}

    public record Instruction(List<Part> parts) {}

    public record Content(String role, List<Part> parts) {}

    public record Part(String text) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GeminiResponse(List<Candidate> candidates) {
        public String extractText() {
            if (candidates == null || candidates.isEmpty()) return null;
            var content = candidates.getFirst().content();
            if (content == null || content.parts() == null || content.parts().isEmpty()) return null;
            return content.parts().getFirst().text();
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Candidate(Content content) {}
}