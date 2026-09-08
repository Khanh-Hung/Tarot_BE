package tarot.application.features.profile.dtos;

public record UserQuotaDto(
    int availableReadings,
    int dailyFreeRemaining,
    int dailyFreeLimit,
    int bonusReadings,
    int adsWatchedToday,
    int maxAdsPerDay,
    boolean canWatchAd,
    int currentStreak,
    int longestStreak,
    boolean isStreakActiveToday
) {
    public UserQuotaDto(
        int availableReadings,
        int dailyFreeRemaining,
        int dailyFreeLimit,
        int bonusReadings,
        int adsWatchedToday,
        int maxAdsPerDay,
        boolean canWatchAd,
        int currentStreak,
        int longestStreak,
        boolean isStreakActiveToday
    ) {
        this.availableReadings = availableReadings;
        this.dailyFreeRemaining = dailyFreeRemaining;
        this.dailyFreeLimit = dailyFreeLimit;
        this.bonusReadings = bonusReadings;
        this.adsWatchedToday = adsWatchedToday;
        this.maxAdsPerDay = maxAdsPerDay;
        this.canWatchAd = canWatchAd;
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.isStreakActiveToday = isStreakActiveToday;
    }
}
