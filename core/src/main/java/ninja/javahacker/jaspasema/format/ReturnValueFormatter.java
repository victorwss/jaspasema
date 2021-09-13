package ninja.javahacker.jaspasema.format;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.EmptyDateFormatException;
import ninja.javahacker.jaspasema.exceptions.badmapping.InvalidDateFormatException;
import ninja.javahacker.jaspasema.exceptions.badmapping.ReturnTypeRestrictionViolationException;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface ReturnValueFormatter<E> {

    @Nullable
    public String make(@Nullable E in);

    @SuppressFBWarnings({"LEST_LOST_EXCEPTION_STACK_TRACE", "BED_HIERARCHICAL_EXCEPTION_DECLARATION"})
    public static <E> ReturnValueFormatter<E> prepare(
            @NonNull ReifiedGeneric<E> target,
            @NonNull String format,
            @NonNull Supplier<ReturnTypeRestrictionViolationException> w,
            @NonNull Supplier<ReturnTypeRestrictionViolationException> x,
            @NonNull Supplier<EmptyDateFormatException> y,
            @NonNull Supplier<InvalidDateFormatException> z)
            throws ReturnTypeRestrictionViolationException, EmptyDateFormatException, InvalidDateFormatException
    {
        @SuppressWarnings("unchecked")
        var simpleFormatter = (FormatterFunction<E>) FormatterMap.FORMAT_MAP.get(target);
        if (simpleFormatter != null) {
            if (!format.isEmpty()) throw w.get();
            return simpleFormatter::format;
        }

        @SuppressWarnings("unchecked")
        var dateFormatter = (Function<DateTimeFormatter, FormatterFunction<E>>) FormatterMap.FORMATTER_DT_MAP.get(target);
        if (dateFormatter == null) throw x.get();

        if (format.isEmpty()) throw y.get();
        DateTimeFormatter dtf;
        try {
            dtf = DateTimeFormatter.ofPattern(format).withResolverStyle(ResolverStyle.STRICT);
        } catch (IllegalArgumentException e) {
            throw z.get();
        }

        return (dateFormatter.apply(dtf))::format;
    }
}