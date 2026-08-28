package tarot.infrastructure.persistence.repositories.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tarot.domain.entities.core.Card;
import tarot.domain.enums.DeckCode;

import java.util.List;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {
    List<Card> findByDeckCode(DeckCode deckCode);
}