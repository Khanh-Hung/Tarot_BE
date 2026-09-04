package tarot.application.features.profile.queries.getmyprofile;

import tarot.domain.entities.identity.User;
import tarot.domain.entities.core.UserProfile;
import tarot.domain.enums.ZodiacSign;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ProfileDto(
        UUID userId,
        String email,
        String userName,
        String displayName,
        String avatarUrl,
        boolean isEmailVerified,
        ZodiacSign zodiacSign,
        UUID favoriteDeckId
) {
    public static ProfileDto fromEntity(User user, UserProfile profile) {
        if (user == null) return null;
        return new ProfileDto(
                user.getId(),
                user.getEmail(),
                user.getUserName(),
                user.getDisplayName(),
                user.getAvatarUrl(),
                user.isEmailVerified(),
                profile != null ? profile.getZodiacSign() : null,
                profile != null ? profile.getFavoriteDeckId() : null
        );
    }
}

