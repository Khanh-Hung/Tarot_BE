package tarot.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    MALE,
    FEMALE,
    OTHER,
    UNKNOWN;

    @JsonValue
    public String getValue() {
        return name();
    }

    @JsonCreator
    public static Gender fromString(String value) {
        if (value == null || value.isBlank()) {
            return UNKNOWN;
        }
        String clean = value.trim().toUpperCase();
        for (Gender g : Gender.values()) {
            if (g.name().equalsIgnoreCase(clean)) {
                return g;
            }
        }
        if (clean.startsWith("MALE") || clean.equals("M")) return MALE;
        if (clean.startsWith("FEMALE") || clean.equals("F")) return FEMALE;
        if (clean.startsWith("OTHER") || clean.equals("O")) return OTHER;

        return UNKNOWN;
    }
}
