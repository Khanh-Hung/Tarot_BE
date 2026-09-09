package tarot.application.features.profile.dtos;

public record BirthCardDto(
    int cardNumber,
    String cardNameVi,
    String cardNameEn,
    String soulCardNameVi,
    String imageUrl,
    String keywords,
    String description
) {}
