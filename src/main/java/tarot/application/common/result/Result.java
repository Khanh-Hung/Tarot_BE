package tarot.application.common.result;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Result Pattern trong Java 21 dùng sealed interface
 */
public sealed interface Result<T> permits Result.Success, Result.Failure {

    record Success<T>(T data) implements Result<T> {
        public Success {
            Objects.requireNonNull(data, "Success data cannot be null");
        }
    }

    record Failure<T>(Error error) implements Result<T> {
        public Failure {
            Objects.requireNonNull(error, "Failure error cannot be null");
        }
    }

    static <T> Result<T> success(T data) {
        return new Success<>(data);
    }

    static <T> Result<T> failure(Error error) {
        return new Failure<>(error);
    }

    default boolean isSuccess() {
        return this instanceof Success<T>;
    }

    default boolean isFailure() {
        return this instanceof Failure<T>;
    }

    default T getDataOrNull() {
        return switch (this) {
            case Success<T> s -> s.data();
            case Failure<T> f -> null;
        };
    }

    default Error getErrorOrNone() {
        return switch (this) {
            case Success<T> s -> Error.NONE;
            case Failure<T> f -> f.error();
        };
    }

    default <R> Result<R> map(Function<? super T, ? extends R> mapper) {
        return switch (this) {
            case Success<T> s -> Result.success(mapper.apply(s.data()));
            case Failure<T> f -> Result.failure(f.error());
        };
    }

    default Result<T> onSuccess(Consumer<? super T> action) {
        if (this instanceof Success<T> s) {
            action.accept(s.data());
        }
        return this;
    }

    default Result<T> onFailure(Consumer<? super Error> action) {
        if (this instanceof Failure<T> f) {
            action.accept(f.error());
        }
        return this;
    }
}