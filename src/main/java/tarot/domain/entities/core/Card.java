package tarot.domain.entities.core;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SoftDelete;
import tarot.domain.common.BaseEntity;
import tarot.domain.enums.ArcanaType;
import tarot.domain.enums.DeckCode;

@Entity
@Table(name = "\"Cards\"")
@org.hibernate.annotations.SQLRestriction("\"Deleted\" = false")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class Card extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "\"DeckCode\"", nullable = false, length = 50)
    @Builder.Default
    private DeckCode deckCode = DeckCode.RIDER_WAITE_CLASSIC;

    @Column(name = "\"NameEn\"", nullable = false, length = 100)
    private String nameEn;

    @Column(name = "\"NameVi\"", nullable = false, length = 100)
    private String nameVi;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"ArcanaType\"", nullable = false, length = 30)
    private ArcanaType arcanaType;

    @Column(name = "\"Element\"", nullable = false, length = 50)
    private String element;

    @Column(name = "\"Keywords\"", nullable = false, length = 255)
    private String keywords;

    @Column(name = "\"KeywordsEn\"", length = 255)
    private String keywordsEn;

    @Column(name = "\"ImageUrl\"", length = 500)
    private String imageUrl;

    @Column(name = "\"UprightMeaning\"", nullable = false, columnDefinition = "TEXT")
    private String uprightMeaning;

    @Column(name = "\"ReversedMeaning\"", nullable = false, columnDefinition = "TEXT")
    private String reversedMeaning;

    @Column(name = "\"UprightMeaningEn\"", columnDefinition = "TEXT")
    private String uprightMeaningEn;

    @Column(name = "\"ReversedMeaningEn\"", columnDefinition = "TEXT")
    private String reversedMeaningEn;
}