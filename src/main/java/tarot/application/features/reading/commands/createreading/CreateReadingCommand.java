package tarot.application.features.reading.commands.createreading;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tarot.domain.enums.DeckCode;
import tarot.domain.enums.SpreadType;
import tarot.domain.enums.Topic;

public record CreateReadingCommand(
    @NotNull(message = "User ID is required")
    Long userId,

    @NotBlank(message = "Question cannot be blank")
    String userQuestion,

    @NotNull(message = "Topic is required")
    Topic topic,

    SpreadType spreadType,
    DeckCode deckCode
) {}