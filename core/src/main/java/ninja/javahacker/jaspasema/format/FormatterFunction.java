package ninja.javahacker.jaspasema.format;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.function.Function;
import lombok.NonNull;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * Function responsible for formatting some object into a {@link String} representation.
 * @param <E> The type of object received as input.
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface FormatterFunction<E> extends Function<E, String> {

    @SuppressWarnings("unchecked")
    public static <E> Optional<FormatterFunction<E>> findIntrinsicSimpleFormatter(@NonNull ReifiedGeneric<E> target) {
        return Optional.ofNullable((FormatterFunction<E>) FormatterMap.FORMAT_MAP.get(target));
    }

    @SuppressWarnings("unchecked")
    public static <E> Optional<Function<DateTimeFormatter, FormatterFunction<E>>> findIntrinsicDateFormatter(@NonNull ReifiedGeneric<E> target) {
        return Optional.ofNullable((Function<DateTimeFormatter, FormatterFunction<E>>) FormatterMap.FORMATTER_DT_MAP.get(target));
    }
}