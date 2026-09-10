package tarot.application.features.profile.commands.claimadreward;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tarot.application.common.result.Error;
import tarot.application.common.result.Result;
import tarot.application.dto.AccountUserDto;
import tarot.application.features.profile.dtos.UserQuotaDto;
import tarot.application.interfaces.AccountServiceClient;
import tarot.domain.entities.core.UserQuota;
import tarot.domain.entities.core.UserStreak;
import tarot.infrastructure.persistence.repositories.core.UserQuotaRepository;
import tarot.infrastructure.persistence.repositories.core.UserStreakRepository;

import tarot.domain.common.datetime.Clock;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClaimAdRewardHandler {

    private final AccountServiceClient accountServiceClient;
    private final UserQuotaRepository quotaRepository;
    private final UserStreakRepository streakRepository;

    public ClaimAdRewardHandler(
            AccountServiceClient accountServiceClient,
            UserQuotaRepository quotaRepository,
            UserStreakRepository streakRepository
    ) {
        this.accountServiceClient = accountServiceClient;
        this.quotaRepository = quotaRepository;
        this.streakRepository = streakRepository;
    }

    @Transactional
    public Result<UserQuotaDto> handle(UUID userId) {
        if (userId == null) {
            return Result.failure(new Error("USER_REQUIRED", "User ID is required"));
        }

        Optional<AccountUserDto> userOpt = accountServiceClient.getUser(userId);
        if (userOpt.isEmpty()) {
            return Result.failure(new Error("USER_NOT_FOUND", "User not found with ID: " + userId));
        }

        LocalDate today = Clock.today();

        UserQuota quota = quotaRepository.findByUserId(userId).orElse(null);
        if (quota == null) {
            quota = UserQuota.createDefault(userId);
        }

        if (!quota.canWatchAd(today)) {
            return Result.failure(new Error(
                    "DAILY_AD_LIMIT_REACHED",
                    "Daily rewarded ad limit reached (maximum " + quota.getMaxAdsPerDay() + " ads per day). Please try again tomorrow."
            ));
        }

        boolean rewarded = quota.addAdRewardBonus(today);
        if (!rewarded) {
            return Result.failure(new Error("CLAIM_REWARD_FAILED", "Unable to claim ad reward at this time. Please try again later."));
        }

        quota = quotaRepository.save(quota);

        UserStreak streak = streakRepository.findByUserId(userId).orElse(null);
        if (streak == null) {
            streak = UserStreak.createDefault(userId);
            streak = streakRepository.save(streak);
        }

        UserQuotaDto dto = new UserQuotaDto(
                quota.getAvailableReadings(),
                quota.getDailyFreeRemaining(),
                quota.getDailyFreeLimit(),
                quota.getBonusReadings(),
                quota.getAdsWatchedToday(),
                quota.getMaxAdsPerDay(),
                quota.canWatchAd(today),
                streak.getEffectiveCurrentStreak(today),
                streak.getLongestStreak(),
                streak.isStreakActiveToday(today)
        );

        return Result.success(dto);
    }
}
