package tarot.domain.common;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public abstract class BaseEntity {

    @Id
    @Column(name = "\"Id\"", nullable = false, updatable = false)
    @Builder.Default
    private UUID id = UuidCreator.getTimeOrderedEpoch();

    @PrePersist
    protected void ensureId() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
    }

    @CreatedDate
    @Column(name = "\"CreatedAt\"", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "\"CreatedBy\"", length = 100)
    private String createdBy;

    @LastModifiedDate
    @Column(name = "\"UpdatedAt\"")
    private LocalDateTime updatedAt;

    @LastModifiedBy
    @Column(name = "\"UpdatedBy\"", length = 100)
    private String updatedBy;

    @Column(name = "\"Deleted\"", nullable = false)
    @Builder.Default
    private boolean deleted = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}