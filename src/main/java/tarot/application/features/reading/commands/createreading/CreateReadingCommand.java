package tarot.application.features.reading.commands.createreading;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tarot.domain.enums.DeckCode;
import tarot.domain.enums.RelationshipStatus;
import tarot.domain.enums.SpreadType;
import tarot.domain.enums.ZodiacSign;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateReadingCommand(
    @NotNull(message = "User ID is required")
    UUID userId,

    @NotBlank(message = "Question cannot be blank")
    String userQuestion,

    ZodiacSign zodiacSign,
    LocalDate dateOfBirth,
    RelationshipStatus relationshipStatus,
    SpreadType spreadType,
    DeckCode deckCode,
    List<UUID> selectedCardIds,
    List<Boolean> isReversedList
) {}