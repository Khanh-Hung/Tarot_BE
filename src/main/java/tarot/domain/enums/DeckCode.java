package tarot.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum DeckCode {
    RIDER_WAITE_CLASSIC, // Bộ Rider-Waite-Smith Cổ Điển 1909
    THOTH_ALEISTER,      // Bộ Thoth Tarot (Aleister Crowley 1944)
    MARSEILLE_HERMETIC;  // Bộ Tarot de Marseille (Pháp cổ điển thế kỷ 17)

    @JsonCreator
    public static DeckCode fromString(String value) {
        if (value == null) return RIDER_WAITE_CLASSIC;
        String val = value.trim().toUpperCase();
        for (DeckCode d : values()) {
            if (d.name().equalsIgnoreCase(val)) return d;
        }
        if (val.contains("THOTH")) return THOTH_ALEISTER;
        if (val.contains("MARSEILLE")) return MARSEILLE_HERMETIC;
        return RIDER_WAITE_CLASSIC;
    }
}