package tarot.application.features.reading.commands.createreading;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tarot.domain.enums.DeckCode;
import tarot.domain.enums.SpreadType;
import tarot.domain.enums.ZodiacSign;

public record CreateReadingCommand(
    @NotNull(message = "User ID is required")
    Long userId,

    @NotBlank(message = "Question cannot be blank")
    String userQuestion,

    ZodiacSign zodiacSign,
    SpreadType spreadType,
    DeckCode deckCode
) {}