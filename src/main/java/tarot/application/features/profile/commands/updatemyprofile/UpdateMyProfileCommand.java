package tarot.application.features.profile.commands.updatemyprofile;

import tarot.domain.enums.ZodiacSign;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record UpdateMyProfileCommand(
        String displayName,
        String avatarUrl,
        ZodiacSign zodiacSign,
        UUID favoriteDeckId
) {}

