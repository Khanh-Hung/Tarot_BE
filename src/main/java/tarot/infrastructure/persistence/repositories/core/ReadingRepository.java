package tarot.infrastructure.persistence.repositories.core;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tarot.domain.entities.core.Reading;

import java.util.UUID;

@Repository
public interface ReadingRepository extends JpaRepository<Reading, UUID> {
    Page<Reading> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}