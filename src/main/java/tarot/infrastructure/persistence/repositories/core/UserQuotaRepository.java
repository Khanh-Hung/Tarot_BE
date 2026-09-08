package tarot.infrastructure.persistence.repositories.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tarot.domain.entities.core.UserQuota;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserQuotaRepository extends JpaRepository<UserQuota, UUID> {
    Optional<UserQuota> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
}
