package tarot.application.features.card.queries.getcardsbydeck;

import tarot.domain.entities.core.Card;

import java.util.UUID;

public record CardDto(
    UUID id,
    String deckCode,
    String nameEn,
    String nameVi,
    String arcanaType,
    String element,
    String keywords,
    String keywordsEn,
    String imageUrl,
    String uprightMeaning,
    String reversedMeaning,
    String uprightMeaningEn,
    String reversedMeaningEn
) {
    public static CardDto fromEntity(Card c) {
        if (c == null) return null;
        return new CardDto(
            c.getId(),
            c.getDeckCode().name(),
            c.getNameEn(),
            c.getNameVi(),
            c.getArcanaType().name(),
            c.getElement(),
            c.getKeywords(),
            c.getKeywordsEn(),
            c.getImageUrl(),
            c.getUprightMeaning(),
            c.getReversedMeaning(),
            c.getUprightMeaningEn(),
            c.getReversedMeaningEn()
        );
    }
}