package tarot.application.features.deck.queries.getdecks;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tarot.application.common.result.Result;
import tarot.infrastructure.persistence.repositories.DeckRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDecksHandler {

    private final DeckRepository deckRepository;

    public Result<List<DeckDto>> handle() {
        List<DeckDto> list = deckRepository.findAll().stream()
                .map(DeckDto::fromEntity)
                .toList();
        return Result.success(list);
    }
}