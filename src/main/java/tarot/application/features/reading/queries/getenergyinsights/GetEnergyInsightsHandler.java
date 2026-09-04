package tarot.application.features.reading.queries.getenergyinsights;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tarot.application.common.result.Result;
import tarot.domain.entities.core.Card;
import tarot.domain.entities.core.DrawnCard;
import tarot.domain.entities.core.Reading;
import tarot.domain.entities.core.UserProfile;
import tarot.infrastructure.ai.core.AiModelClient;
import tarot.infrastructure.persistence.repositories.core.ReadingRepository;
import tarot.infrastructure.persistence.repositories.core.UserProfileRepository;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetEnergyInsightsHandler {

    private final ReadingRepository readingRepository;
    private final UserProfileRepository userProfileRepository;
    private final AiModelClient aiModelClient;

    @Transactional(readOnly = true)
    public Result<EnergyInsightsResponse> handle(UUID userId) {
        List<Reading> readings = readingRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        if (readings == null || readings.isEmpty()) {
            return Result.success(new EnergyInsightsResponse(
                0, 0,
                Map.of("FIRE", 0L, "WATER", 0L, "AIR", 0L, "EARTH", 0L),
                Map.of("FIRE", 0.0, "WATER", 0.0, "AIR", 0.0, "EARTH", 0.0),
                "BALANCE", "Cân Bằng",
                Collections.emptyList(),
                Collections.emptyMap(),
                "Bạn chưa có quẻ bói nào. Hãy thực hiện bốc bài để bắt đầu xây dựng Bản đồ Năng lượng của riêng mình nhé!"
            ));
        }

        long totalReadings = readings.size();

        // Thu thập tất cả các lá bài đã rút
        List<DrawnCard> allDrawnCards = readings.stream()
            .filter(r -> r.getDrawnCards() != null)
            .flatMap(r -> r.getDrawnCards().stream())
            .filter(dc -> dc.getCard() != null)
            .toList();

        long totalCards = allDrawnCards.size();

        // 1. Phân bổ Chuẩn Quốc Tế: 4 Nguyên Tố (Fire, Water, Air, Earth)
        Map<String, Long> elementCounts = new HashMap<>();
        elementCounts.put("FIRE", 0L);
        elementCounts.put("WATER", 0L);
        elementCounts.put("AIR", 0L);
        elementCounts.put("EARTH", 0L);

        for (DrawnCard dc : allDrawnCards) {
            Card card = dc.getCard();
            String elem = card.getElement() != null ? card.getElement().toUpperCase().trim() : "";
            if (elem.contains("FIRE") || elem.contains("LỬA")) {
                elementCounts.put("FIRE", elementCounts.get("FIRE") + 1);
            } else if (elem.contains("WATER") || elem.contains("NƯỚC")) {
                elementCounts.put("WATER", elementCounts.get("WATER") + 1);
            } else if (elem.contains("AIR") || elem.contains("KHÍ")) {
                elementCounts.put("AIR", elementCounts.get("AIR") + 1);
            } else {
                elementCounts.put("EARTH", elementCounts.get("EARTH") + 1);
            }
        }

        Map<String, Double> elementPercentages = new HashMap<>();
        for (Map.Entry<String, Long> entry : elementCounts.entrySet()) {
            double pct = (totalCards > 0) ? Math.round((entry.getValue() * 100.0 / totalCards) * 10.0) / 10.0 : 0.0;
            elementPercentages.put(entry.getKey(), pct);
        }

        // Tìm nguyên tố chủ đạo
        String dominantElement = "BALANCE";
        long maxCount = 0;
        for (Map.Entry<String, Long> entry : elementCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                dominantElement = entry.getKey();
            }
        }

        String dominantElementVi = switch (dominantElement) {
            case "FIRE" -> "Lửa";
            case "WATER" -> "Nước";
            case "AIR" -> "Khí";
            case "EARTH" -> "Đất";
            default -> "Cân Bằng";
        };

        // 2. Top các lá bài xuất hiện nhiều nhất
        Map<UUID, List<DrawnCard>> cardsGrouped = allDrawnCards.stream()
            .collect(Collectors.groupingBy(dc -> dc.getCard().getId()));

        List<EnergyInsightsResponse.FrequentCardDto> topCards = cardsGrouped.values().stream()
            .map(list -> {
                DrawnCard first = list.get(0);
                Card c = first.getCard();
                long count = list.size();
                double pct = (totalCards > 0) ? Math.round((count * 100.0 / totalCards) * 10.0) / 10.0 : 0.0;
                long upright = list.stream().filter(dc -> !dc.isReversed()).count();
                long reversed = list.stream().filter(DrawnCard::isReversed).count();

                return new EnergyInsightsResponse.FrequentCardDto(
                    c.getId() != null ? c.getId().toString() : "",
                    c.getNameVi(),
                    c.getNameEn(),
                    c.getImageUrl(),
                    c.getElement(),
                    count,
                    pct,
                    upright,
                    reversed
                );
            })
            .sorted(Comparator.comparingLong(EnergyInsightsResponse.FrequentCardDto::count).reversed())
            .limit(3)
            .toList();

        // 3. Phân bổ Chủ Đề (Topic)
        Map<String, Long> topicDistribution = readings.stream()
            .filter(r -> r.getTopic() != null)
            .collect(Collectors.groupingBy(r -> r.getTopic().name(), Collectors.counting()));

        // 4. Lời khuyên Năng lượng: FAST-PATH (trả về tức thì trong 5ms) + Background AI Refresh
        Reading latestReading = readings.get(0);
        UserProfile userProfile = userProfileRepository.findByUserId(userId).orElse(null);

        String energyAdvice = null;
        if (userProfile != null
                && userProfile.getCachedLastReadingId() != null
                && userProfile.getCachedLastReadingId().equals(latestReading.getId())
                && userProfile.getCachedEnergyAdvice() != null
                && !userProfile.getCachedEnergyAdvice().isBlank()) {
            // Lấy ngay từ cache đã có (0ms, 0 chi phí token AI)
            energyAdvice = userProfile.getCachedEnergyAdvice();
        } else {
            // Dùng ngay lời khuyên chuẩn mẫu để trang render tức thì (0ms, không bắt người dùng chờ)
            energyAdvice = switch (dominantElement) {
                case "FIRE" -> "Năng lượng Lửa đang dẫn lối bạn với đam mê và khát khao hành động mãnh liệt. Hãy cẩn trọng tránh sự bốc đồng và kiên nhẫn hơn với các mục tiêu dài hạn.";
                case "WATER" -> "Năng lượng Nước đang bao bọc tâm hồn bạn, nhấn mạnh trực giác và thế giới cảm xúc sâu sắc. Đây là lúc để bạn lắng nghe linh cảm và chữa lành nội tâm.";
                case "AIR" -> "Năng lượng Khí đang chiếm ưu thế, cho thấy bạn là người suy nghĩ sắc bén nhưng đôi khi bị quá tải bởi âu lo. Hãy cho tâm trí khoảng lặng để thư thái.";
                case "EARTH" -> "Năng lượng Đất vững chãi đang hỗ trợ bạn xây dựng nền tảng tài chính, công việc và sự nghiệp thực tế. Hãy tiếp tục kiên trì với từng bước đi chắc chắn.";
                default -> "Năng lượng của bạn đang ở trạng thái cân bằng tuyệt đối giữa các phương diện của cuộc sống.";
            };

            // Kích hoạt tiến trình ngầm (Async Background) gọi AI sinh lời khuyên cá nhân hóa sâu sắc
            final UUID finalUserId = userId;
            final UUID lastReadingId = latestReading.getId();
            final String lastQuestion = latestReading.getUserQuestion();
            final String domElement = dominantElementVi;
            final double domPct = elementPercentages.getOrDefault(dominantElement, 0.0);
            final String cardsText = topCards.stream().map(EnergyInsightsResponse.FrequentCardDto::nameVi).collect(Collectors.joining(", "));

            CompletableFuture.runAsync(() -> {
                try {
                    String sysInstruction = "Bạn là chuyên gia cố vấn năng lượng Tarot chiêm tinh. Hãy viết một đoạn phân tích và lời khuyên năng lượng ngắn gọn (đúng 2 đến 3 câu, khoảng 45-65 từ), giọng văn sâu sắc, ấm áp, định hướng hành động tích cực và chữa lành. TUYỆT ĐỐI KHÔNG dùng tiếng Anh, không xưng là AI hay trợ lý, không chào hỏi rườm rà. Viết trực tiếp vào thông điệp.";

                    String userPrompt = String.format(
                        "Dữ liệu năng lượng thực tế của người dùng:\n" +
                        "- Nguyên tố chủ đạo hiện tại: %s (chiếm %s%%)\n" +
                        "- Các lá bài gắn liền nhất gần đây: %s\n" +
                        "- Câu hỏi trăn trở gần nhất của người dùng: \"%s\"\n" +
                        "Dựa trên các dữ liệu trên, hãy viết thông điệp lời khuyên năng lượng 2-3 câu cho người này.",
                        domElement,
                        domPct,
                        cardsText.isBlank() ? "Đa dạng các lá bài" : cardsText,
                        lastQuestion
                    );

                    String aiGenerated = aiModelClient.generateContent(sysInstruction, userPrompt);
                    if (aiGenerated != null && !aiGenerated.isBlank()) {
                        String cleanAdvice = aiGenerated.trim().replaceAll("^\"|\"$", "");
                        UserProfile profileToUpdate = userProfileRepository.findByUserId(finalUserId).orElse(null);
                        if (profileToUpdate != null) {
                            profileToUpdate.updateEnergyAdvice(cleanAdvice, lastReadingId);
                            userProfileRepository.save(profileToUpdate);
                            log.info("✅ Đã cập nhật xong Lời khuyên năng lượng AI ngầm cho User: {}", finalUserId);
                        }
                    }
                } catch (Exception ex) {
                    log.warn("Không thể sinh lời khuyên năng lượng AI ngầm: {}", ex.getMessage());
                }
            });
        }

        return Result.success(new EnergyInsightsResponse(
            totalReadings,
            totalCards,
            elementCounts,
            elementPercentages,
            dominantElement,
            dominantElementVi,
            topCards,
            topicDistribution,
            energyAdvice
        ));
    }
}