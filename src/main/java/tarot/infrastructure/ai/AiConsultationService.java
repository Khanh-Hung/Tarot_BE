package tarot.infrastructure.ai;

import tarot.domain.entities.ChatMessage;
import tarot.domain.entities.DrawnCard;
import tarot.domain.entities.User;
import tarot.domain.enums.SpreadType;
import tarot.domain.enums.ZodiacSign;
import tarot.infrastructure.ai.core.AiReadingResult;

import java.util.List;

public interface AiConsultationService {

    AiReadingResult generateInitialReading(
        User user,
        ZodiacSign zodiacSign,
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
}