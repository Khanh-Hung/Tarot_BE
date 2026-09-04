package tarot.application.features.profile.commands.updatemyprofile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tarot.domain.enums.ZodiacSign;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UpdateMyProfileCommand(
        String displayName,
        ZodiacSign zodiacSign,
        UUID favoriteDeckId
) {}

