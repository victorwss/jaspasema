package ninja.javahacker.jaspasema.format;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
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

    /**
     * Finds the {@link FormatterFunction} that is responsible for formatting a simple type.
     * By simple type, we means as a primitive type, a primitive type wrapper, {@link String}, {@link BigInteger} or
     * {@link BigDecimal}.
     * @param <E> The type of the {@link FormatterFunction}.
     * @param target The {@link ReifiedGeneric} instance representing the type.
     * @return An {@link Optional} containing the {@link FormatterFunction} for the represented type or an empty one
     *     if the {@code target} is not of a simple type.
     */
    @SuppressWarnings("unchecked")
    public static <E> Optional<FormatterFunction<E>> findIntrinsicSimpleFormatter(@NonNull ReifiedGeneric<E> target) {
        return Optional.ofNullable((FormatterFunction<E>) FormatterMap.FORMAT_MAP.get(target));
    }

    /**
     * Finds the {@link Function} that, by receiving the {@link DateTimeFormatter} for formatting a date/time object,
     * is responsible for giving a {@link FormatterFunction} that formats such date/time object.
     * The accepted date/time objects are of the classes {@link LocalDate},  {@link LocalDateTime}, {@link LocalTime},
     * {@link Year}, {@link YearMonth}, {@link OffsetDateTime} and {@link ZonedDateTime}.
     * @param <E> The type of the {@link FormatterFunction}.
     * @param target The {@link ReifiedGeneric} instance representing the type.
     * @return An {@link Optional} containing the aforementioned {@link Function} or an empty one
     *     if the {@code target} is not of an acceptable date/time class.
     */
    @SuppressWarnings("unchecked")
    public static <E> Optional<Function<DateTimeFormatter, FormatterFunction<E>>>
            findIntrinsicDateFormatter(@NonNull ReifiedGeneric<E> target)
    {
        return Optional.ofNullable((Function<DateTimeFormatter, FormatterFunction<E>>) FormatterMap.FORMATTER_DT_MAP.get(target));
    }
}