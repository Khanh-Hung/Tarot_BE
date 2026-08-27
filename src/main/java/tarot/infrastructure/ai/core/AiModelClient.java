package tarot.infrastructure.ai.core;

/**
 * Strategy Interface: Trừu tượng hóa lời gọi tới các mô hình AI LLM (Gemini, OpenAI, Claude, Local)
 */
public interface AiModelClient {
    String generateContent(String systemInstruction, String userPrompt);
}