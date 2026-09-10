package tarot.application.features.profile.commands.updatemyprofile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tarot.application.common.result.Error;
import tarot.application.common.result.Result;
import tarot.application.dto.AccountUserDto;
import tarot.application.features.profile.queries.getmyprofile.ProfileDto;
import tarot.application.interfaces.AccountServiceClient;
import tarot.domain.entities.core.UserProfile;
import tarot.domain.entities.core.UserStreak;
import tarot.domain.enums.ZodiacSign;
import tarot.infrastructure.persistence.repositories.core.UserProfileRepository;
import tarot.infrastructure.persistence.repositories.core.UserStreakRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateMyProfileHandler {

    private final AccountServiceClient accountServiceClient;
    private final UserProfileRepository profileRepository;
    private final UserStreakRepository streakRepository;

    @Transactional
    public Result<ProfileDto> handle(UUID userId, UpdateMyProfileCommand command) {
        Optional<AccountUserDto> userOpt = accountServiceClient.getUser(userId);
        if (userOpt.isEmpty()) {
            return Result.failure(new Error("USER_NOT_FOUND", "User not found with ID: " + userId));
        }

        AccountUserDto user = userOpt.get();

        // Cập nhật cài đặt riêng Tarot vào bảng UserProfiles
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> UserProfile.createDefault(userId, null));

        ZodiacSign resolvedZodiac = (command.zodiacSign() != null && command.zodiacSign() != ZodiacSign.UNKNOWN)
                ? command.zodiacSign()
                : (command.dateOfBirth() != null
                    ? ZodiacSign.fromLocalDate(command.dateOfBirth())
                    : (user.dateOfBirth() != null
                        ? ZodiacSign.fromLocalDate(user.dateOfBirth())
                        : profile.getZodiacSign()));

        profile.updatePreferences(
                resolvedZodiac,
                command.favoriteDeckId(),
                command.relationshipStatus()
        );
        UserProfile savedProfile = profileRepository.save(profile);
        UserStreak streak = streakRepository.findByUserId(userId).orElse(null);

        return Result.success(ProfileDto.from(user, savedProfile, streak));
    }
}
