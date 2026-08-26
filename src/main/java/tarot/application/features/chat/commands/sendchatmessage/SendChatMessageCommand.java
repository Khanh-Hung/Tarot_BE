package tarot.application.features.chat.commands.sendchatmessage;

import jakarta.validation.constraints.NotBlank;

public record SendChatMessageCommand(
    @NotBlank(message = "Message content cannot be blank")
    String message
) {}