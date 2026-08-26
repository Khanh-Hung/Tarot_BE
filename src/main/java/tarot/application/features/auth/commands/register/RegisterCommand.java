package tarot.application.features.auth.commands.register;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import tarot.domain.enums.ZodiacSign;

public record RegisterCommand(
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    String email,

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    String password,

    String fullName,

    ZodiacSign zodiacSign
) {}