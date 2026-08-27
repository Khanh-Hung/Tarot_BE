package tarot.application.features.reading.commands.createreading;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tarot.application.common.result.Error;
import tarot.application.common.result.Result;
import tarot.domain.entities.Card;
import tarot.domain.entities.Reading;
import tarot.domain.entities.User;
import tarot.domain.enums.DeckCode;
import tarot.domain.enums.ZodiacSign;
import tarot.infrastructure.ai.AiConsultationService;
import tarot.infrastructure.ai.core.AiReadingResult;
import tarot.infrastructure.persistence.repositories.CardRepository;
import tarot.infrastructure.persistence.repositories.ReadingRepository;
import tarot.infrastructure.persistence.repositories.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateReadingHandler {

    private final UserRepository userRepository;
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

        // 2. Kiểm tra & Tự động lưu Cung hoàng đạo nếu User chưa có
        if (user.getZodiacSign() == ZodiacSign.UNKNOWN) {
            if (command.zodiacSign() == null || command.zodiacSign() == ZodiacSign.UNKNOWN) {
                return Result.failure(new Error("ZODIAC_REQUIRED", "Please select your zodiac sign to enhance reading accuracy."));
            }
            user.updateProfile(user.getUsername(), command.zodiacSign());
            userRepository.save(user);
        }

        // 3. Query available cards
        DeckCode deckCode = (command.deckCode() != null) ? command.deckCode() : DeckCode.RIDER_WAITE_CLASSIC;
        List<Card> allCards = cardRepository.findByDeckCode(deckCode);
        if (allCards.isEmpty()) {
            allCards = cardRepository.findAll();
        }
        if (allCards.isEmpty()) {
            return Result.failure(new Error("CARDS_EMPTY", "Card repository is empty. Please seed cards data."));
        }

        // 4. Domain Logic: Create Aggregate Root & Draw Cards
        Reading reading = Reading.create(user, command.userQuestion(), null, command.spreadType(), deckCode);
        reading.drawCards(allCards);

        // 5. Infrastructure: AI Consultation (AI tự phân tích Topic & Sinh bản luận giải)
        AiReadingResult aiResult = aiService.generateInitialReading(
            user,
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