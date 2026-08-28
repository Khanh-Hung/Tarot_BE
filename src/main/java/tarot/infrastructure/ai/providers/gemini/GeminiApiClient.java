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
            @Value("${gemini.model:gemini-1.5-flash}") String model
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
            throw new IllegalStateException("GEMINI_API_KEY chưa được cấu hình! Vui lòng cung cấp API Key trong application.yml hoặc biến môi trường.");
        }

        // Danh sách các model thử nghiệm theo thứ tự ưu tiên
        List<String> modelsToTry = List.of(
            "gemini-1.5-flash",
            "gemini-2.5-flash",
            "gemini-1.5-pro",
            model
        ).stream().distinct().toList();

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
                log.warn("Gọi model {} thất bại ({}), đang thử model dự phòng tiếp theo...", currentModel, e.getMessage());
            }
        }

        log.error("Tất cả các model Gemini đều thất bại: {}", lastException != null ? lastException.getMessage() : "Unknown");
        throw new RuntimeException("Không thể kết nối đến Google Gemini AI: " + (lastException != null ? lastException.getMessage() : "All models unavailable"), lastException);
    }

    // =========================================================================
    // --- TYPED RECORD DTOs (Ánh xạ chuẩn cấu trúc JSON Google Gemini API) ---
    // =========================================================================

    public record GeminiRequest(
            Instruction system_instruction,
            List<Content> contents
    ) {
        public static GeminiRequest of(String systemPrompt, String userPrompt) {
            return new GeminiRequest(
                    new Instruction(List.of(new Part(systemPrompt))),
                    List.of(new Content("user", List.of(new Part(userPrompt))))
            );
        }
    }

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