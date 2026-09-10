package tarot.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SpreadType {
    DAILY_ORACLE,              // 1 lá: Thông điệp ngày mới
    PAST_PRESENT_FUTURE,       // 3 lá: Quá khứ - Hiện tại - Tương lai
    TWO_PATHS_CHOICE,          // 3 lá: Thực tại - Lựa chọn A - Lựa chọn B
    LOVE_RELATIONSHIP,         // 3 lá: Bạn - Người ấy - Kết nối tương lai
    MIND_BODY_SPIRIT,          // 3 lá: Thân - Tâm - Trí chữa lành
    SITUATION_OBSTACLE_ADVICE, // 3 lá: Thực trạng - Thách thức - Lời khuyên
    HORSESHOE,                 // 5 lá: Móng ngựa may mắn
    CELTIC_CROSS;              // 10 lá: Thập tự Celtic kinh điển

    public int getCardCount() {
        return switch (this) {
            case DAILY_ORACLE -> 1;
            case HORSESHOE -> 5;
            case CELTIC_CROSS -> 10;
            default -> 3;
        };
    }

    @JsonCreator
    public static SpreadType fromString(String value) {
        if (value == null) return PAST_PRESENT_FUTURE;
        String val = value.trim().toUpperCase();
        if ("THREE_CARDS_TIMELINE".equals(val) || "THREE_CARDS".equals(val)) {
            return PAST_PRESENT_FUTURE;
        }
        if ("SINGLE_CARD_FOCUS".equals(val) || "SINGLE_CARD".equals(val)) {
            return DAILY_ORACLE;
        }
        if ("LOVE".equals(val) || "LOVE_CONNECTION".equals(val)) {
            return LOVE_RELATIONSHIP;
        }
        if ("HEALING".equals(val)) {
            return MIND_BODY_SPIRIT;
        }
        if ("CAREER".equals(val) || "PROBLEM_SOLUTION".equals(val)) {
            return SITUATION_OBSTACLE_ADVICE;
        }
        for (SpreadType type : values()) {
            if (type.name().equalsIgnoreCase(val)) {
                return type;
            }
        }
        return PAST_PRESENT_FUTURE;
    }
}