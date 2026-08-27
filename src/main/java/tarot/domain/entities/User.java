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

    @Column(name = "username", length = 100)
    private String username;

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

    // --- DOMAIN FACTORY METHODS ---

    public static User create(String email, String passwordHash) {
        return create(email, passwordHash, null, ZodiacSign.UNKNOWN);
    }

    public static User create(String email, String passwordHash, String username, ZodiacSign zodiacSign) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be blank");
        }

        String defaultUsername = (username != null && !username.isBlank()) 
                ? username.trim() 
                : email.split("@")[0]; // Lấy phần trước @ làm username mặc định (VD: khanhhung@gmail.com -> khanhhung)

        return User.builder()
                .email(email.trim().toLowerCase())
                .passwordHash(passwordHash)
                .username(defaultUsername)
                .zodiacSign(zodiacSign != null ? zodiacSign : ZodiacSign.UNKNOWN)
                .role(UserRole.USER)
                .isActive(true)
                .build();
    }

    // --- DOMAIN BUSINESS METHODS ---

    public void updateProfile(String username, ZodiacSign zodiacSign) {
        if (username != null && !username.isBlank()) {
            this.username = username.trim();
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