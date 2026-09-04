package tarot.application.features.reading.queries.getenergyinsights;

import java.util.List;
import java.util.Map;

public record EnergyInsightsResponse(
    long totalReadings,
    long totalCardsDrawn,
    Map<String, Long> elementCounts,
    Map<String, Double> elementPercentages,
    String dominantElement,
    String dominantElementVi,
    List<FrequentCardDto> topCards,
    Map<String, Long> topicDistribution,
    String energyAdvice
) {
    public record FrequentCardDto(
        String cardCode,
        String nameVi,
        String nameEn,
        String imageUrl,
        String element,
        long count,
        double percentage,
        long uprightCount,
        long reversedCount
    ) {}
}
