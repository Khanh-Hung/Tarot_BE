package tarot.application.features.profile.queries.getmyprofile;

import tarot.domain.entities.User;
import tarot.domain.entities.UserProfile;
import tarot.domain.enums.ZodiacSign;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ProfileDto(
        UUID userId,
        String email,
        String username,
        String role,
        LocalDate birthDate,
        LocalTime birthTime,
        String bio,
        String avatarUrl,
        ZodiacSign zodiacSign,
        UUID favoriteDeckId
) {
    public static ProfileDto fromEntity(User user, UserProfile profile) {
        if (user == null) return null;
        return new ProfileDto(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole().name(),
                user.getBirthDate(),
                user.getBirthTime(),
                user.getBio(),
                user.getAvatarUrl(),
                profile != null ? profile.getZodiacSign() : null,
                profile != null ? profile.getFavoriteDeckId() : null
        );
    }
}

