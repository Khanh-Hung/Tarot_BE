package tarot.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import tarot.domain.common.BaseEntity;
import tarot.domain.enums.ZodiacSign;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class UserProfile extends BaseEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "zodiac_sign", length = 30)
    private ZodiacSign zodiacSign;

    @Column(name = "favorite_deck_id")
    private Long favoriteDeckId;

    // --- DOMAIN FACTORY METHOD ---

    public static UserProfile createDefault(Long userId, ZodiacSign zodiacSign) {
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

    public void updatePreferences(ZodiacSign zodiacSign, Long favoriteDeckId) {
        if (zodiacSign != null) {
            this.zodiacSign = zodiacSign;
        }
        if (favoriteDeckId != null) {
            this.favoriteDeckId = favoriteDeckId;
        }
    }
}



