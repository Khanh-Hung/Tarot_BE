package tarot.application.features.profile.queries.getmyprofile;

import tarot.domain.entities.identity.User;
import tarot.domain.entities.core.UserProfile;
import tarot.domain.entities.core.UserStreak;
import tarot.domain.enums.ZodiacSign;

import java.time.LocalDate;
import java.util.UUID;

public record ProfileDto(
        UUID userId,
        String email,
        String userName,
        String displayName,
        String avatarUrl,
        boolean isEmailVerified,
        ZodiacSign zodiacSign,
        UUID favoriteDeckId,
        int currentStreak,
        int longestStreak,
        boolean isStreakActiveToday
) {
    public static ProfileDto fromEntity(User user, UserProfile profile, UserStreak streak) {
        if (user == null) return null;
        LocalDate today = tarot.domain.common.datetime.Clock.today();
        return new ProfileDto(
                user.getId(),
                user.getEmail(),
                user.getUserName(),
                user.getDisplayName(),
                user.getAvatarUrl(),
                user.isEmailVerified(),
                profile != null ? profile.getZodiacSign() : null,
                profile != null ? profile.getFavoriteDeckId() : null,
                streak != null ? streak.getEffectiveCurrentStreak(today) : 0,
                streak != null ? streak.getLongestStreak() : 0,
                streak != null && streak.isStreakActiveToday(today)
        );
    }
}
