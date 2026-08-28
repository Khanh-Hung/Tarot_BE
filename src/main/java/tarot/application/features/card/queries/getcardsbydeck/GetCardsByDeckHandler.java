package tarot.application.features.card.queries.getcardsbydeck;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tarot.application.common.result.Result;
import tarot.domain.enums.DeckCode;
import tarot.infrastructure.persistence.repositories.core.CardRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetCardsByDeckHandler {

    private final CardRepository cardRepository;

    public Result<List<CardDto>> handle(DeckCode deckCode) {
        List<CardDto> list = cardRepository.findByDeckCode(deckCode).stream()
                .map(CardDto::fromEntity)
                .toList();
        return Result.success(list);
    }
}