package tarot.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Topic {
    LOVE_AND_RELATIONSHIP,
    CAREER_AND_FINANCE,
    SELF_GROWTH_AND_HEALING,
    GENERAL_GUIDANCE;

    @JsonCreator
    public static Topic fromString(String value) {
        if (value == null) return GENERAL_GUIDANCE;
        String val = value.trim().toUpperCase();
        if (val.contains("LOVE") || val.contains("RELATIONSHIP")) return LOVE_AND_RELATIONSHIP;
        if (val.contains("CAREER") || val.contains("MONEY") || val.contains("FINANCE")) return CAREER_AND_FINANCE;
        if (val.contains("GROWTH") || val.contains("HEALING") || val.contains("SPIRITUAL")) return SELF_GROWTH_AND_HEALING;
        for (Topic t : values()) {
            if (t.name().equalsIgnoreCase(val)) return t;
        }
        return GENERAL_GUIDANCE;
    }
}