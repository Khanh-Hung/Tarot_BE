package tarot.infrastructure.persistence.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tarot.domain.entities.Reading;

@Repository
public interface ReadingRepository extends JpaRepository<Reading, Long> {
    Page<Reading> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}