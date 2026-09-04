package tarot.domain.entities.core;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import tarot.domain.common.BaseEntity;
import tarot.domain.enums.ZodiacSign;

import java.util.UUID;

@Entity
@Table(name = "\"UserProfiles\"")
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class UserProfile extends BaseEntity {

    @Column(name = "\"UserId\"", nullable = false, unique = true)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"ZodiacSign\"", length = 30)
    private ZodiacSign zodiacSign;

    @Column(name = "\"FavoriteDeckId\"")
    private UUID favoriteDeckId;

    @Column(name = "\"CachedEnergyAdvice\"", columnDefinition = "TEXT")
    private String cachedEnergyAdvice;

    @Column(name = "\"CachedLastReadingId\"")
    private UUID cachedLastReadingId;

    // --- DOMAIN FACTORY METHOD ---

    public static UserProfile createDefault(UUID userId, ZodiacSign zodiacSign) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return UserProfile.builder()
                .userId(userId)
                .zodiacSign(zodiacSign)
                .build();
    }

    // --- DOMAIN BUSINESS METHODS ---

    public void updateZodiac(ZodiacSign zodiacSign) {
        if (zodiacSign != null) {
            this.zodiacSign = zodiacSign;
        }
    }

    public void updatePreferences(ZodiacSign zodiacSign, UUID favoriteDeckId) {
        if (zodiacSign != null) {
            this.zodiacSign = zodiacSign;
        }
        if (favoriteDeckId != null) {
            this.favoriteDeckId = favoriteDeckId;
        }
    }

    public void updateEnergyAdvice(String advice, UUID lastReadingId) {
        this.cachedEnergyAdvice = advice;
        this.cachedLastReadingId = lastReadingId;
    }
}



