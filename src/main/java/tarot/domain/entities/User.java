package tarot.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SoftDelete;
import tarot.domain.common.BaseEntity;
import tarot.domain.enums.UserRole;

import java.time.LocalDate;
import java.time.LocalTime;

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

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "birth_time")
    private LocalTime birthTime;

    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    @Builder.Default
    private UserRole role = UserRole.USER;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    // --- DOMAIN FACTORY METHODS ---

    public static User create(String email, String passwordHash) {
        return create(email, passwordHash, null);
    }

    public static User create(String email, String passwordHash, String username) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be blank");
        }

        String defaultUsername = (username != null && !username.isBlank()) 
                ? username.trim() 
                : email.split("@")[0]; // Lấy phần trước @ làm username mặc định

        return User.builder()
                .email(email.trim().toLowerCase())
                .passwordHash(passwordHash)
                .username(defaultUsername)
                .role(UserRole.USER)
                .isActive(true)
                .build();
    }

    // --- DOMAIN BUSINESS METHODS ---

    public void updatePersonalInfo(String username, LocalDate birthDate, LocalTime birthTime, String bio, String avatarUrl) {
        if (username != null && !username.isBlank()) {
            this.username = username.trim();
        }
        if (birthDate != null) {
            this.birthDate = birthDate;
        }
        if (birthTime != null) {
            this.birthTime = birthTime;
        }
        if (bio != null) {
            this.bio = bio.trim();
        }
        if (avatarUrl != null) {
            this.avatarUrl = avatarUrl.trim();
        }
    }

    public void changePassword(String newPasswordHash) {
        if (newPasswordHash == null || newPasswordHash.isBlank()) {
            throw new IllegalArgumentException("New password hash cannot be blank");
        }
        this.passwordHash = newPasswordHash;
    }
}