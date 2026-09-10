package tarot.application.features.profile.queries.getmyprofile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tarot.application.common.result.Error;
import tarot.application.common.result.Result;
import tarot.application.dto.AccountUserDto;
import tarot.application.interfaces.AccountServiceClient;
import tarot.domain.entities.core.UserProfile;
import tarot.domain.entities.core.UserStreak;
import tarot.infrastructure.persistence.repositories.core.UserProfileRepository;
import tarot.infrastructure.persistence.repositories.core.UserStreakRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetMyProfileHandler {

    private final AccountServiceClient accountServiceClient;
    private final UserProfileRepository profileRepository;
    private final UserStreakRepository streakRepository;

    public Result<ProfileDto> handle(UUID userId) {
        Optional<AccountUserDto> userOpt = accountServiceClient.getUser(userId);
        if (userOpt.isEmpty()) {
            return Result.failure(new Error("USER_NOT_FOUND", "User not found with ID: " + userId));
        }

        AccountUserDto user = userOpt.get();
        UserProfile profile = profileRepository.findByUserId(userId).orElse(null);
        UserStreak streak = streakRepository.findByUserId(userId).orElse(null);
        return Result.success(ProfileDto.from(user, profile, streak));
    }
}
