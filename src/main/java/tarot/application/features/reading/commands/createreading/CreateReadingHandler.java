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
import tarot.infrastructure.ai.AiConsultationService;
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

        // 2. Query available cards
        DeckCode deckCode = (command.deckCode() != null) ? command.deckCode() : DeckCode.RIDER_WAITE_CLASSIC;
        List<Card> allCards = cardRepository.findByDeckCode(deckCode);
        if (allCards.isEmpty()) {
            allCards = cardRepository.findAll();
        }
        if (allCards.isEmpty()) {
            return Result.failure(new Error("CARDS_EMPTY", "Card repository is empty. Please seed cards data."));
        }

        // 3. Domain Logic: Create Aggregate Root & Draw Cards
        Reading reading = Reading.create(user, command.userQuestion(), command.topic(), command.spreadType(), deckCode);
        reading.drawCards(allCards);

        // 4. Infrastructure: AI Consultation
        String aiMarkdown = aiService.generateInitialReading(
            user,
            reading.getUserQuestion(),
            reading.getTopic(),
            reading.getSpreadType(),
            reading.getDrawnCards()
        );
        reading.attachInitialReading(aiMarkdown);

        // 5. Persistence & Output Mapping
        Reading saved = readingRepository.save(reading);
        return Result.success(CreateReadingResponse.fromEntity(saved));
    }
}