package tarot.domain.common.result;

public record Error(String code, String message) {
    public static final Error NONE = new Error("", "");
    public static final Error NULL_VALUE = new Error("Error.NullValue", "Giá trị truyền vào bị null.");

    public boolean isNone() {
        return this.equals(NONE);
    }
}