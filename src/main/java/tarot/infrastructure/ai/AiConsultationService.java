package tarot.infrastructure.ai;

import tarot.application.dto.AccountUserDto;
import tarot.domain.entities.core.ChatMessage;
import tarot.domain.entities.core.DrawnCard;
import tarot.domain.enums.RelationshipStatus;
import tarot.domain.enums.SpreadType;
import tarot.domain.enums.ZodiacSign;
import tarot.infrastructure.ai.core.AiReadingResult;

import java.util.List;

public interface AiConsultationService {

    AiReadingResult generateInitialReading(
        AccountUserDto user,
        ZodiacSign zodiacSign,
        RelationshipStatus relationshipStatus,
        String userQuestion,
        SpreadType spreadType,
        List<DrawnCard> drawnCards
    );

    String generateChatReply(
        String userQuestion,
        List<DrawnCard> drawnCards,
        List<ChatMessage> history,
        String userNewMessage
    );

    String generateConclusionQuote(
        String userQuestion,
        List<DrawnCard> drawnCards,
        String initialReading
    );
}