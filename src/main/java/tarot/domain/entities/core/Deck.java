package tarot.domain.entities.core;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import tarot.domain.common.BaseEntity;
import tarot.domain.enums.DeckCode;

@Entity
@Table(name = "\"Decks\"")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class Deck extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "\"Code\"", nullable = false, unique = true, length = 50)
    private DeckCode code;

    @Column(name = "\"NameVi\"", nullable = false, length = 100)
    private String nameVi;

    @Column(name = "\"NameEn\"", nullable = false, length = 100)
    private String nameEn;

    @Column(name = "\"Description\"", columnDefinition = "TEXT")
    private String description;

    @Column(name = "\"CoverImageUrl\"", length = 500)
    private String coverImageUrl;
}