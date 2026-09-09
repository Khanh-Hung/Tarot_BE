package tarot.application.features.profile.queries.getmyprofile;

import tarot.application.features.profile.dtos.BirthCardDto;
import tarot.domain.common.TarotBirthCardCalculator;
import tarot.domain.common.datetime.Clock;
import tarot.domain.entities.identity.User;
import tarot.domain.entities.core.UserProfile;
import tarot.domain.entities.core.UserStreak;
import tarot.domain.enums.Gender;
import tarot.domain.enums.RelationshipStatus;
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
        LocalDate dateOfBirth,
        Gender gender,
        RelationshipStatus relationshipStatus,
        ZodiacSign zodiacSign,
        UUID favoriteDeckId,
        BirthCardDto birthCard,
        int currentStreak,
        int longestStreak,
        boolean isStreakActiveToday
) {
    public static ProfileDto fromEntity(User user, UserProfile profile, UserStreak streak) {
        if (user == null) return null;
        LocalDate today = Clock.today();
        BirthCardDto birthCard = (user.getDateOfBirth() != null)
                ? TarotBirthCardCalculator.calculate(user.getDateOfBirth())
                : null;

        return new ProfileDto(
                user.getId(),
                user.getEmail(),
                user.getUserName(),
                user.getDisplayName(),
                user.getAvatarUrl(),
                user.isEmailVerified(),
                user.getDateOfBirth(),
                user.getGender(),
                profile != null ? profile.getRelationshipStatus() : RelationshipStatus.SINGLE,
                profile != null ? profile.getZodiacSign() : null,
                profile != null ? profile.getFavoriteDeckId() : null,
                birthCard,
                streak != null ? streak.getEffectiveCurrentStreak(today) : 0,
                streak != null ? streak.getLongestStreak() : 0,
                streak != null && streak.isStreakActiveToday(today)
        );
    }
}
