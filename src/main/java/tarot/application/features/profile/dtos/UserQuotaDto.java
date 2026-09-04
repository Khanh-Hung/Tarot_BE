package tarot.application.features.profile.dtos;

public record UserQuotaDto(
    int availableReadings,
    int dailyFreeRemaining,
    int dailyFreeLimit,
    int bonusReadings,
    int adsWatchedToday,
    int maxAdsPerDay,
    boolean canWatchAd
) {}
