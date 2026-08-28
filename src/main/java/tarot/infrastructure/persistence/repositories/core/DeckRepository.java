package tarot.infrastructure.persistence.repositories.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tarot.domain.entities.core.Deck;
import tarot.domain.enums.DeckCode;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeckRepository extends JpaRepository<Deck, UUID> {
    Optional<Deck> findByCode(DeckCode code);
}