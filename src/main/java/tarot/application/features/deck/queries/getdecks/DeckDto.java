package tarot.application.features.deck.queries.getdecks;

import tarot.domain.entities.Deck;

import java.util.UUID;

public record DeckDto(
    UUID id,
    String code,
    String nameVi,
    String nameEn,
    String description,
    String coverImageUrl
) {
    public static DeckDto fromEntity(Deck d) {
        if (d == null) return null;
        return new DeckDto(
            d.getId(),
            d.getCode().name(),
            d.getNameVi(),
            d.getNameEn(),
            d.getDescription(),
            d.getCoverImageUrl()
        );
    }
}