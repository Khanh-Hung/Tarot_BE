package tarot.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tarot.domain.enums.Gender;

import java.time.LocalDate;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AccountUserDto(
        UUID userId,
        String userName,
        String displayName,
        String avatarUrl,
        String email,
        LocalDate dateOfBirth,
        Gender gender,
        boolean isEmailVerified
) {
    public String getDisplayName() {
        if (displayName != null && !displayName.isBlank()) {
            return displayName.trim();
        }
        return getCleanUserName();
    }

    public String getUserName() {
        return userName != null ? userName : "";
    }

    public String getCleanUserName() {
        if (userName != null && !userName.isBlank()) {
            return userName.trim();
        }
        return "bạn";
    }

    public String getAvatarUrl() {
        return avatarUrl != null ? avatarUrl : "";
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }
}
