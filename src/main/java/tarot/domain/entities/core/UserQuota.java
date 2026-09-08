package tarot.domain.entities.core;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import tarot.domain.common.BaseEntity;
import tarot.domain.enums.SpreadType;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "\"UserQuotas\"")
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class UserQuota extends BaseEntity {

    public static final int DAILY_FREE_LIMIT = 1;
    public static final int MAX_ADS_PER_DAY = 8;

    @Column(name = "\"UserId\"", nullable = false, unique = true)
    private UUID userId;

    @Builder.Default
    @Column(name = "\"DailyReadingsUsed\"", nullable = false)
    private Integer dailyReadingsUsed = 0;

    @Builder.Default
    @Column(name = "\"BonusReadings\"", nullable = false)
    private Integer bonusReadings = 0;

    @Builder.Default
    @Column(name = "\"AdsWatchedToday\"", nullable = false)
    private Integer adsWatchedToday = 0;

    @Column(name = "\"LastReadingDate\"")
    private LocalDate lastReadingDate;

    // --- DOMAIN FACTORY METHOD ---

    public static UserQuota createDefault(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return UserQuota.builder()
                .userId(userId)
                .dailyReadingsUsed(0)
                .bonusReadings(0)
                .adsWatchedToday(0)
                .lastReadingDate(tarot.domain.common.datetime.Clock.today())
                .build();
    }

    // --- DOMAIN BUSINESS METHODS ---

    public int getDailyReadingsUsed() {
        return dailyReadingsUsed != null ? dailyReadingsUsed : 0;
    }

    public int getBonusReadings() {
        return bonusReadings != null ? bonusReadings : 0;
    }

    public int getAdsWatchedToday() {
        return adsWatchedToday != null ? adsWatchedToday : 0;
    }

    public void checkAndResetDailyQuota(LocalDate today) {
        if (this.lastReadingDate == null || !this.lastReadingDate.isEqual(today)) {
            this.dailyReadingsUsed = 0;
            this.adsWatchedToday = 0;
            this.lastReadingDate = today;
        }
    }

    public int getDailyFreeRemaining() {
        return Math.max(0, DAILY_FREE_LIMIT - getDailyReadingsUsed());
    }

    public int getDailyFreeLimit() {
        return DAILY_FREE_LIMIT;
    }

    public int getMaxAdsPerDay() {
        return MAX_ADS_PER_DAY;
    }

    public int getAvailableReadings() {
        return getDailyFreeRemaining() + getBonusReadings();
    }

    public boolean canPerformReading(LocalDate today, SpreadType spreadType) {
        checkAndResetDailyQuota(today);
        if (spreadType == SpreadType.DAILY_ORACLE) {
            return getDailyFreeRemaining() > 0 || getBonusReadings() > 0;
        }
        // Các trải bài 3 lá hoặc chuyên sâu khác bắt buộc dùng lượt thưởng từ video quảng cáo
        return getBonusReadings() > 0;
    }

    public boolean consumeReading(LocalDate today, SpreadType spreadType) {
        checkAndResetDailyQuota(today);
        if (!canPerformReading(today, spreadType)) {
            return false;
        }
        if (spreadType == SpreadType.DAILY_ORACLE) {
            if (getDailyFreeRemaining() > 0) {
                this.dailyReadingsUsed = getDailyReadingsUsed() + 1;
            } else if (getBonusReadings() > 0) {
                this.bonusReadings = getBonusReadings() - 1;
            }
        } else {
            this.bonusReadings = getBonusReadings() - 1;
        }
        this.lastReadingDate = today;
        return true;
    }

    public boolean consumeReading(LocalDate today) {
        return consumeReading(today, SpreadType.DAILY_ORACLE);
    }

    public boolean canWatchAd(LocalDate today) {
        checkAndResetDailyQuota(today);
        return getAdsWatchedToday() < MAX_ADS_PER_DAY;
    }

    public boolean addAdRewardBonus(LocalDate today) {
        checkAndResetDailyQuota(today);
        if (getAdsWatchedToday() >= MAX_ADS_PER_DAY) {
            return false;
        }
        this.adsWatchedToday = getAdsWatchedToday() + 1;
        this.bonusReadings = getBonusReadings() + 1;
        this.lastReadingDate = today;
        return true;
    }

    public void addBonusReadings(int bonus) {
        if (bonus > 0) {
            this.bonusReadings = getBonusReadings() + bonus;
        }
    }
}
