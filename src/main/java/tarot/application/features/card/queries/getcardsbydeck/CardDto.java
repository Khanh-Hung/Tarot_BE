package tarot.application.features.card.queries.getcardsbydeck;

import tarot.domain.entities.Card;

public record CardDto(
    Long id,
    String deckCode,
    String nameEn,
    String nameVi,
    String arcanaType,
    String element,
    String keywords,
    String imageUrl,
    String uprightMeaning,
    String reversedMeaning
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
            c.getImageUrl(),
            c.getUprightMeaning(),
            c.getReversedMeaning()
        );
    }
}