package tarot.domain.common.result;

public sealed interface Result<T> permits Result.Success, Result.Failure {

    record Success<T>(T data) implements Result<T> {}
    record Failure<T>(Error error) implements Result<T> {}

    default boolean isSuccess() {
        return this instanceof Success<T>;
    }

    default boolean isFailure() {
        return !isSuccess();
    }

    default T getDataOrNull() {
        return (this instanceof Success<T> s) ? s.data() : null;
    }

    default Error getErrorOrNone() {
        return (this instanceof Failure<T> f) ? f.error() : Error.NONE;
    }

    static <T> Result<T> success(T data) {
        return new Success<>(data);
    }

    static <T> Result<T> success() {
        return new Success<>(null);
    }

    static <T> Result<T> failure(Error error) {
        return new Failure<>(error);
    }
}