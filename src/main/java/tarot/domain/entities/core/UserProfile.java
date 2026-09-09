package tarot.domain.entities.core;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import tarot.domain.common.BaseEntity;
import tarot.domain.enums.RelationshipStatus;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "\"RelationshipStatus\"", length = 30)
    private RelationshipStatus relationshipStatus;

    // --- DOMAIN FACTORY METHOD ---

    public static UserProfile createDefault(UUID userId, ZodiacSign zodiacSign) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return UserProfile.builder()
                .userId(userId)
                .zodiacSign(zodiacSign)
                .relationshipStatus(tarot.domain.enums.RelationshipStatus.SINGLE)
                .build();
    }

    // --- DOMAIN BUSINESS METHODS ---

    public void updateZodiac(ZodiacSign zodiacSign) {
        if (zodiacSign != null) {
            this.zodiacSign = zodiacSign;
        }
    }

    public void updateRelationshipStatus(tarot.domain.enums.RelationshipStatus relationshipStatus) {
        if (relationshipStatus != null && relationshipStatus != tarot.domain.enums.RelationshipStatus.UNKNOWN) {
            this.relationshipStatus = relationshipStatus;
        }
    }

    public void updatePreferences(ZodiacSign zodiacSign, UUID favoriteDeckId, tarot.domain.enums.RelationshipStatus relationshipStatus) {
        if (zodiacSign != null) {
            this.zodiacSign = zodiacSign;
        }
        if (favoriteDeckId != null) {
            this.favoriteDeckId = favoriteDeckId;
        }
        if (relationshipStatus != null && relationshipStatus != tarot.domain.enums.RelationshipStatus.UNKNOWN) {
            this.relationshipStatus = relationshipStatus;
        }
    }

    public void updatePreferences(ZodiacSign zodiacSign, UUID favoriteDeckId) {
        updatePreferences(zodiacSign, favoriteDeckId, null);
    }

    public void updateEnergyAdvice(String advice, UUID lastReadingId) {
        this.cachedEnergyAdvice = advice;
        this.cachedLastReadingId = lastReadingId;
    }

    public RelationshipStatus getRelationshipStatus() {
        return relationshipStatus != null ? relationshipStatus : RelationshipStatus.SINGLE;
    }

    public ZodiacSign getZodiacSign() {
        return zodiacSign;
    }

    public UUID getFavoriteDeckId() {
        return favoriteDeckId;
    }

    public String getCachedEnergyAdvice() {
        return cachedEnergyAdvice;
    }

    public UUID getCachedLastReadingId() {
        return cachedLastReadingId;
    }
}
