package soup.modelchecker;

import java.util.function.Function;

// Represents a computation that either succeeds (Ok) or fails (Err)
public sealed interface Result<T> permits Result.Ok, Result.Err {

    // --- Success case ---
    record Ok<T>(T value) implements Result<T> {}

    // --- Error case ---
    record Err<T>(Throwable error) implements Result<T> {}

    // --- Factory methods ---
    static <T> Result<T> ok(T value) { return new Ok<>(value); }
    static <T> Result<T> err(Throwable e) { return new Err<>(e); }

    // --- Monadic operations ---

    // map :: (T -> U) -> Result<U>
    default <U> Result<U> map(java.util.function.Function<? super T, ? extends U> f) {
        return switch (this) {
            case Ok<T>(var v) -> {
                try { yield ok(f.apply(v)); }
                catch (Throwable e) { yield err(e); }
            }
            case Err<T>(var e) -> err(e);
        };
    }

    // flatMap :: (T -> Result<U>) -> Result<U>
    default <U> Result<U> flatMap(java.util.function.Function<? super T, Result<U>> f) {
        return switch (this) {
            case Ok<T>(var v) -> {
                try { yield f.apply(v); }
                catch (Throwable e) { yield err(e); }
            }
            case Err<T>(var e) -> err(e);
        };
    }

    // recover :: (Throwable -> T) -> Result<T>
    default Result<T> recover(java.util.function.Function<? super Throwable, ? extends T> handler) {
        return switch (this) {
            case Ok<T>(var v) -> this;
            case Err<T>(var e) -> {
                try { yield ok(handler.apply(e)); }
                catch (Throwable ex) { yield err(ex); }
            }
        };
    }

    // unwrap / getOrElse
    default T getOrElse(T fallback) {
        return switch (this) {
            case Ok<T>(var v) -> v;
            case Err<T>(var e) -> fallback;
        };
    }

    // isOk / isErr helpers
    default boolean isOk()  { return this instanceof Ok<T>; }
    default boolean isErr() { return this instanceof Err<T>; }

    default <U> Result<U> andThen(Function<? super T, Result<U>> f) {
        return flatMap(f);
    }

    default Result<T> mapErr(Function<? super Throwable, ? extends Throwable> f) {
        return switch (this) {
            case Ok<T>(var v) -> this;
            case Err<T>(var e) -> Result.err(f.apply(e));
        };
    }
}