package tarot.application.features.reading.commands.createreading;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tarot.application.common.result.Error;
import tarot.application.common.result.Result;
import tarot.domain.entities.core.Card;
import tarot.domain.entities.core.Reading;
import tarot.domain.entities.identity.User;
import tarot.domain.entities.core.UserProfile;
import tarot.domain.entities.core.UserQuota;
import tarot.domain.entities.core.UserStreak;
import tarot.domain.enums.DeckCode;
import tarot.domain.enums.SpreadType;
import tarot.domain.enums.ZodiacSign;
import tarot.infrastructure.ai.AiConsultationService;
import tarot.infrastructure.ai.core.AiReadingResult;
import tarot.infrastructure.persistence.repositories.core.CardRepository;
import tarot.infrastructure.persistence.repositories.core.ReadingRepository;
import tarot.infrastructure.persistence.repositories.core.UserProfileRepository;
import tarot.infrastructure.persistence.repositories.core.UserQuotaRepository;
import tarot.infrastructure.persistence.repositories.core.UserStreakRepository;
import tarot.infrastructure.persistence.repositories.identity.UserRepository;

import tarot.domain.common.datetime.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateReadingHandler {

    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;
    private final UserQuotaRepository quotaRepository;
    private final UserStreakRepository streakRepository;
    private final CardRepository cardRepository;
    private final ReadingRepository readingRepository;
    private final AiConsultationService aiService;

    @Transactional
    public Result<CreateReadingResponse> handle(CreateReadingCommand command) {
        // 1. Query & Validate User
        User user = userRepository.findById(command.userId()).orElse(null);
        if (user == null) {
            return Result.failure(new Error("USER_NOT_FOUND", "User not found with ID: " + command.userId()));
        }

        // 2. Kiểm tra Cung hoàng đạo
        UserProfile profile = profileRepository.findByUserId(user.getId()).orElse(null);
        LocalDate today = Clock.today();

        if (profile == null) {
            profile = UserProfile.createDefault(user.getId(), command.zodiacSign());
            profileRepository.save(profile);
        }

        ZodiacSign zodiac = (profile.getZodiacSign() != null)
                ? profile.getZodiacSign()
                : command.zodiacSign();

        if (zodiac == null) {
            return Result.failure(new Error("ZODIAC_REQUIRED", "Please select your zodiac sign to enhance reading accuracy."));
        }

        // 3. Kiểm tra và trừ Hạn mức lượt bói (UserQuota)
        UserQuota quota = quotaRepository.findByUserId(user.getId()).orElse(null);
        if (quota == null) {
            quota = UserQuota.createDefault(user.getId());
        } else {
            quota.checkAndResetDailyQuota(today);
        }

        if (!quota.canPerformReading(today, command.spreadType())) {
            if (command.spreadType() != SpreadType.DAILY_ORACLE && quota.getBonusReadings() <= 0) {
                return Result.failure(new Error(
                        "AD_REQUIRED_FOR_SPREAD",
                        "Watching a rewarded ad is required to unlock this spread. Please watch an ad to earn energy."
                ));
            }
            return Result.failure(new Error(
                    "DAILY_QUOTA_EXCEEDED",
                    "Daily reading quota exceeded. Please watch a rewarded video to earn extra readings."
            ));
        }

        quota.consumeReading(today, command.spreadType());

        // 4. Ghi nhận chuỗi bốc bài hàng ngày (UserStreak)
        UserStreak streak = streakRepository.findByUserId(user.getId()).orElse(null);
        if (streak == null) {
            streak = UserStreak.createDefault(user.getId());
        }
        int bonusAwarded = streak.recordDailyStreak(today);
        streakRepository.save(streak);

        // Nếu chạm mốc thưởng chuỗi ngày -> Cộng thêm bonus readings vào Quota
        if (bonusAwarded > 0) {
            quota.addBonusReadings(bonusAwarded);
        }
        quotaRepository.save(quota);

        // 5. Query available cards
        DeckCode deckCode = (command.deckCode() != null) ? command.deckCode() : DeckCode.RIDER_WAITE_CLASSIC;
        List<Card> allCards = cardRepository.findByDeckCode(deckCode);
        if (allCards.isEmpty()) {
            allCards = cardRepository.findAll();
        }
        if (allCards.isEmpty()) {
            return Result.failure(new Error("CARDS_EMPTY", "Card repository is empty. Please seed cards data."));
        }

        // 4. Domain Logic: Create Aggregate Root & Draw Cards
        Reading reading = Reading.create(user.getId(), command.userQuestion(), null, command.spreadType(), deckCode);

        if (command.selectedCardIds() != null && !command.selectedCardIds().isEmpty()) {
            // Lấy đúng các lá bài mà người dùng đã tự tay bốc từ giao diện
            List<Card> selected = command.selectedCardIds().stream()
                    .filter(id -> id != null)
                    .map(id -> cardRepository.findById(id).orElse(null))
                    .filter(c -> c != null)
                    .toList();
            if (!selected.isEmpty()) {
                reading.drawSelectedCards(selected, command.isReversedList());
            } else {
                reading.drawCards(allCards);
            }
        } else {
            // Tự động xáo ngẫu nhiên
            reading.drawCards(allCards);
        }

        // 5. Infrastructure: AI Consultation (AI tự phân tích Topic & Sinh bản luận giải)
        AiReadingResult aiResult = aiService.generateInitialReading(
            user,
            zodiac,
            reading.getUserQuestion(),
            reading.getSpreadType(),
            reading.getDrawnCards()
        );

        reading.updateTopic(aiResult.detectedTopic());
        reading.attachInitialReading(aiResult.markdownContent());

        // 6. Persistence & Output Mapping
        Reading saved = readingRepository.save(reading);
        return Result.success(CreateReadingResponse.fromEntity(saved));
    }
}