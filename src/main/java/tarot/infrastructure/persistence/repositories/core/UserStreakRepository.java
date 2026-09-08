package tarot.infrastructure.persistence.repositories.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tarot.domain.entities.core.UserStreak;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserStreakRepository extends JpaRepository<UserStreak, UUID> {
    Optional<UserStreak> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
}
