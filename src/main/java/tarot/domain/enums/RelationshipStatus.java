package tarot.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RelationshipStatus {
    SINGLE,
    DATING,
    IN_RELATIONSHIP,
    COMPLICATED,
    MARRIED,
    UNKNOWN;

    @JsonValue
    public String getValue() {
        return name();
    }

    @JsonCreator
    public static RelationshipStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return UNKNOWN;
        }
        String clean = value.trim().toUpperCase();
        for (RelationshipStatus s : RelationshipStatus.values()) {
            if (s.name().equalsIgnoreCase(clean)) {
                return s;
            }
        }
        if (clean.contains("SINGLE")) return SINGLE;
        if (clean.contains("DATING")) return DATING;
        if (clean.contains("RELATIONSHIP")) return IN_RELATIONSHIP;
        if (clean.contains("COMPLICATED")) return COMPLICATED;
        if (clean.contains("MARRIED")) return MARRIED;

        return UNKNOWN;
    }
}
