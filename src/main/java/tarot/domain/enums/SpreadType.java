package tarot.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SpreadType {
    PAST_PRESENT_FUTURE, // 3 lá: Quá khứ - Hiện tại - Tương lai
    TWO_PATHS_CHOICE,    // 3 lá: Thực tại - Lựa chọn A - Lựa chọn B
    DAILY_ORACLE;        // 1 lá: Thông điệp ngày mới

    @JsonCreator
    public static SpreadType fromString(String value) {
        if (value == null) return PAST_PRESENT_FUTURE;
        String val = value.trim().toUpperCase();
        if ("THREE_CARDS_TIMELINE".equals(val) || "THREE_CARDS".equals(val) || "CELTIC_CROSS".equals(val)) {
            return PAST_PRESENT_FUTURE;
        }
        if ("SINGLE_CARD_FOCUS".equals(val) || "SINGLE_CARD".equals(val)) {
            return DAILY_ORACLE;
        }
        for (SpreadType type : values()) {
            if (type.name().equalsIgnoreCase(val)) {
                return type;
            }
        }
        return PAST_PRESENT_FUTURE;
    }
}