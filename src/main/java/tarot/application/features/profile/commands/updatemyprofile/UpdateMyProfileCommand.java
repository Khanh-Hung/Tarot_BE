package tarot.application.features.profile.commands.updatemyprofile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tarot.domain.enums.Gender;
import tarot.domain.enums.RelationshipStatus;
import tarot.domain.enums.ZodiacSign;

import java.time.LocalDate;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UpdateMyProfileCommand(
        String displayName,
        LocalDate dateOfBirth,
        Gender gender,
        ZodiacSign zodiacSign,
        UUID favoriteDeckId,
        RelationshipStatus relationshipStatus
) {}

