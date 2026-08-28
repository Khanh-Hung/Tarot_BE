package tarot.domain.entities.identity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SoftDelete;
import tarot.domain.common.BaseEntity;
import tarot.domain.enums.UserRole;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "\"Users\"")
@org.hibernate.annotations.SQLRestriction("\"Deleted\" = false")
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class User extends BaseEntity {

    @Column(name = "\"Email\"", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "\"PasswordHash\"", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "\"UserName\"", length = 100)
    private String userName;

    @Column(name = "\"DisplayName\"", length = 100)
    private String displayName;

    @Column(name = "\"AvatarUrl\"", length = 500)
    private String avatarUrl;

    @Column(name = "\"LastUserNameChangedAt\"")
    private java.time.LocalDateTime lastUserNameChangedAt;

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

        String defaultUserName = (username != null && !username.isBlank()) 
                ? username.trim() 
                : email.split("@")[0];

        return User.builder()
                .email(email.trim().toLowerCase())
                .passwordHash(passwordHash)
                .userName(defaultUserName)
                .displayName(defaultUserName)
                .avatarUrl("")
                .build();
    }

    // --- DOMAIN BUSINESS METHODS ---

    public void updateProfile(String displayName, String avatarUrl) {
        if (displayName != null) {
            this.displayName = displayName.trim();
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

    public String getUserName() {
        return userName != null && !userName.isBlank() ? userName : email;
    }

    public String getUsername() {
        return getUserName();
    }

    public String getDisplayName() {
        return displayName != null && !displayName.isBlank() ? displayName : getUsername();
    }

    public String getAvatarUrl() {
        return avatarUrl != null ? avatarUrl : "";
    }

    public UserRole getRole() {
        return UserRole.USER;
    }

    public boolean isActive() {
        return true;
    }
}