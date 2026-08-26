package tarot.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SoftDelete;
import tarot.domain.common.BaseEntity;
import tarot.domain.enums.UserRole;
import tarot.domain.enums.ZodiacSign;

@Entity
@Table(name = "users")
@SoftDelete
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class User extends BaseEntity {

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "zodiac_sign", nullable = false, length = 30)
    @Builder.Default
    private ZodiacSign zodiacSign = ZodiacSign.UNKNOWN;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    @Builder.Default
    private UserRole role = UserRole.USER;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    // --- DOMAIN FACTORY METHOD ---

    public static User create(String email, String passwordHash, String fullName, ZodiacSign zodiacSign) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be blank");
        }

        return User.builder()
                .email(email.trim().toLowerCase())
                .passwordHash(passwordHash)
                .fullName((fullName != null && !fullName.isBlank()) ? fullName.trim() : "Oracle Seeker")
                .zodiacSign(zodiacSign != null ? zodiacSign : ZodiacSign.UNKNOWN)
                .role(UserRole.USER)
                .isActive(true)
                .build();
    }

    // --- DOMAIN BUSINESS METHODS ---

    public void updateProfile(String fullName, ZodiacSign zodiacSign) {
        if (fullName != null && !fullName.isBlank()) {
            this.fullName = fullName.trim();
        }
        if (zodiacSign != null) {
            this.zodiacSign = zodiacSign;
        }
    }

    public void changePassword(String newPasswordHash) {
        if (newPasswordHash == null || newPasswordHash.isBlank()) {
            throw new IllegalArgumentException("New password hash cannot be blank");
        }
        this.passwordHash = newPasswordHash;
    }
}