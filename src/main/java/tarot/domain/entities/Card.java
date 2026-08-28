package tarot.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SoftDelete;
import tarot.domain.common.BaseEntity;
import tarot.domain.enums.ArcanaType;
import tarot.domain.enums.DeckCode;

@Entity
@Table(name = "cards")
@SoftDelete // 🔥 Xóa mềm tự động
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class Card extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "deck_code", nullable = false, length = 50)
    @Builder.Default
    private DeckCode deckCode = DeckCode.RIDER_WAITE_CLASSIC;

    @Column(name = "name_en", nullable = false, length = 100)
    private String nameEn;

    @Column(name = "name_vi", nullable = false, length = 100)
    private String nameVi;

    @Enumerated(EnumType.STRING)
    @Column(name = "arcana_type", nullable = false, length = 30)
    private ArcanaType arcanaType;

    @Column(name = "element", nullable = false, length = 50)
    private String element;

    @Column(name = "keywords", nullable = false, length = 255)
    private String keywords;

    @Column(name = "keywords_en", length = 255)
    private String keywordsEn;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "upright_meaning", nullable = false, columnDefinition = "TEXT")
    private String uprightMeaning;

    @Column(name = "reversed_meaning", nullable = false, columnDefinition = "TEXT")
    private String reversedMeaning;

    @Column(name = "upright_meaning_en", columnDefinition = "TEXT")
    private String uprightMeaningEn;

    @Column(name = "reversed_meaning_en", columnDefinition = "TEXT")
    private String reversedMeaningEn;
}