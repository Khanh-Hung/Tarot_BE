package tarot.domain.entities.core;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import tarot.domain.common.BaseEntity;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "\"UserStreaks\"")
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class UserStreak extends BaseEntity {

    @Column(name = "\"UserId\"", nullable = false, unique = true)
    private UUID userId;

    @Builder.Default
    @Column(name = "\"CurrentStreak\"", nullable = false)
    private Integer currentStreak = 0;

    @Builder.Default
    @Column(name = "\"LongestStreak\"", nullable = false)
    private Integer longestStreak = 0;

    @Column(name = "\"LastStreakDate\"")
    private LocalDate lastStreakDate;

    // --- DOMAIN FACTORY METHOD ---

    public static UserStreak createDefault(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return UserStreak.builder()
                .userId(userId)
                .currentStreak(0)
                .longestStreak(0)
                .lastStreakDate(null)
                .build();
    }

    // --- DOMAIN BUSINESS METHODS ---

    public int getCurrentStreak() {
        return currentStreak != null ? currentStreak : 0;
    }

    public int getLongestStreak() {
        return longestStreak != null ? longestStreak : 0;
    }

    public LocalDate getLastStreakDate() {
        return lastStreakDate;
    }

    public boolean isStreakActiveToday(LocalDate today) {
        return today != null && lastStreakDate != null && lastStreakDate.isEqual(today);
    }

    public int getEffectiveCurrentStreak(LocalDate today) {
        if (today == null || lastStreakDate == null) {
            return 0;
        }
        if (lastStreakDate.isEqual(today) || lastStreakDate.isEqual(today.minusDays(1))) {
            return getCurrentStreak();
        }
        return 0;
    }

    /**
     * Ghi nhận điểm danh streak cho ngày hôm nay và trả về số lượt bốc bài thưởng (bonus) nếu chạm mốc
     * @param today ngày hiện tại
     * @return số lượt bonus được trao tặng (0 nếu không có thưởng)
     */
    public int recordDailyStreak(LocalDate today) {
        if (today == null) {
            return 0;
        }

        // Đã điểm danh hôm nay rồi
        if (lastStreakDate != null && lastStreakDate.isEqual(today)) {
            return 0;
        }

        // Nếu ngày điểm danh gần nhất là hôm qua -> Tăng streak
        if (lastStreakDate != null && lastStreakDate.isEqual(today.minusDays(1))) {
            this.currentStreak = getCurrentStreak() + 1;
        } else {
            // Đứt chuỗi -> Reset về 1
            this.currentStreak = 1;
        }

        if (this.currentStreak > getLongestStreak()) {
            this.longestStreak = this.currentStreak;
        }
        this.lastStreakDate = today;

        // Tính toán phần thưởng mốc gắn kết
        int bonusAwarded = 0;
        if (this.currentStreak == 3) {
            bonusAwarded = 1;
        } else if (this.currentStreak == 7) {
            bonusAwarded = 3;
        } else if (this.currentStreak == 14) {
            bonusAwarded = 5;
        } else if (this.currentStreak == 30) {
            bonusAwarded = 10;
        } else if (this.currentStreak == 60) {
            bonusAwarded = 15;
        } else if (this.currentStreak == 100) {
            bonusAwarded = 20;
        } else if (this.currentStreak == 365) {
            bonusAwarded = 50;
        } else if (this.currentStreak > 30 && (this.currentStreak - 30) % 7 == 0) {
            // Chu kỳ tuần hoàn vô tận sau 30 ngày: Cứ mỗi 7 ngày thưởng +3 lượt
            bonusAwarded = 3;
        }

        return bonusAwarded;
    }
}
