package tarot.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tarot.domain.entities.Deck;
import tarot.domain.enums.DeckCode;

import java.util.Optional;

@Repository
public interface DeckRepository extends JpaRepository<Deck, Long> {
    Optional<Deck> findByCode(DeckCode code);
}