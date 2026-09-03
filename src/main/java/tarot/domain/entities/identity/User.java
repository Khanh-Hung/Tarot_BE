package tarot.domain.entities.identity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import tarot.domain.common.BaseEntity;
import tarot.domain.enums.UserRole;

@Entity
@Table(name = "\"Users\"")
@AttributeOverride(name = "deleted", column = @Column(name = "\"IsSoftDeleted\"", nullable = false))
@org.hibernate.annotations.SQLRestriction("\"IsSoftDeleted\" = false")
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

    @Convert(converter = tarot.infrastructure.persistence.converters.UserRoleConverter.class)
    @Column(name = "\"Role\"", length = 50, nullable = false)
    private UserRole role;

    @Column(name = "\"IsEmailVerified\"", nullable = false)
    private boolean isEmailVerified;

    @Column(name = "\"LastUserNameChangedAt\"")
    private java.time.LocalDateTime lastUserNameChangedAt;

    // --- DOMAIN FACTORY METHODS ---

    public static User create(String email, String passwordHash) {
        return create(email, passwordHash, null);
    }

    public static User create(String email, String passwordHash, String username) {
        return create(email, passwordHash, username, UserRole.USER, false);
    }

    public static User create(String email, String passwordHash, String username, UserRole role, boolean isEmailVerified) {
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
                .role(role != null ? role : UserRole.USER)
                .isEmailVerified(isEmailVerified)
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

    public void verifyEmail() {
        this.isEmailVerified = true;
    }

    public void updateRole(UserRole newRole) {
        if (newRole != null) {
            this.role = newRole;
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

    public boolean isActive() {
        return true;
    }
}