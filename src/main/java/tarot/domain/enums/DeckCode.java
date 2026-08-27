package tarot.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum DeckCode {
    RIDER_WAITE_CLASSIC, // Bộ Cổ Điển 1909
    ANIME_MANGA,         // Bộ Manga Nhật Bản
    CYBERPUNK_NEON,      // Bộ Tương Lai Cyberpunk
    MYSTICAL_CATS;       // Bộ Mèo Huyền Bí

    @JsonCreator
    public static DeckCode fromString(String value) {
        if (value == null) return RIDER_WAITE_CLASSIC;
        String val = value.trim().toUpperCase();
        for (DeckCode d : values()) {
            if (d.name().equalsIgnoreCase(val)) return d;
        }
        return RIDER_WAITE_CLASSIC;
    }
}