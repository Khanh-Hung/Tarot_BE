package tarot.application.features.profile.commands.updatemyprofile;

import tarot.domain.enums.ZodiacSign;

import java.time.LocalDate;
import java.time.LocalTime;

public record UpdateMyProfileCommand(
        String username,
        LocalDate birthDate,
        LocalTime birthTime,
        String bio,
        String avatarUrl,
        ZodiacSign zodiacSign,
        Long favoriteDeckId
) {}
