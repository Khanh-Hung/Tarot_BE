package tarot.application.features.profile.queries.getquota;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tarot.application.common.result.Error;
import tarot.application.common.result.Result;
import tarot.application.features.profile.dtos.UserQuotaDto;
import tarot.domain.entities.core.UserProfile;
import tarot.domain.entities.identity.User;
import tarot.infrastructure.persistence.repositories.core.UserProfileRepository;
import tarot.infrastructure.persistence.repositories.identity.UserRepository;

import tarot.domain.common.datetime.Clock;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetUserQuotaHandler {

    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;

    @Transactional
    public Result<UserQuotaDto> handle(UUID userId) {
        if (userId == null) {
            return Result.failure(new Error("USER_REQUIRED", "User ID is required"));
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Result.failure(new Error("USER_NOT_FOUND", "User not found with ID: " + userId));
        }

        UserProfile profile = profileRepository.findByUserId(userId).orElse(null);
        LocalDate today = Clock.today();

        if (profile == null) {
            profile = UserProfile.createDefault(userId, null);
            profile = profileRepository.save(profile);
        } else {
            profile.checkAndResetDailyQuota(today);
            profile = profileRepository.save(profile);
        }

        UserQuotaDto dto = new UserQuotaDto(
                profile.getAvailableReadings(),
                profile.getDailyFreeRemaining(),
                profile.getDailyFreeLimit(),
                profile.getBonusReadings(),
                profile.getAdsWatchedToday(),
                profile.getMaxAdsPerDay(),
                profile.canWatchAd(today)
        );

        return Result.success(dto);
    }
}
