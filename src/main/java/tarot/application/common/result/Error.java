package tarot.application.common.result;

/**
 * Record đại diện cho thông tin lỗi trả về từ các Use-Case (Application Layer)
 */
public record Error(String code, String message) {
    public static final Error NONE = new Error("", "");
    public static final Error NULL_VALUE = new Error("NULL_VALUE", "The specified value is null.");
}