package ninja.javahacker.jaspasema.format;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.NonNull;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
@SuppressFBWarnings("FII_USE_METHOD_REFERENCE")
public interface FormatterFunction<E> {
    @Nullable
    public String format(@Nullable E value);

    @NonNull
    @SuppressFBWarnings({"LEST_LOST_EXCEPTION_STACK_TRACE", "BED_HIERARCHICAL_EXCEPTION_DECLARATION"})
    public static <E, W extends Throwable, X extends Throwable, Y extends Throwable, Z extends Throwable> FormatterFunction<E> formatterFor(
            @NonNull String format,
            @NonNull ReifiedGeneric<E> target,
            @NonNull Supplier<W> unmappeableTarget,
            @NonNull Supplier<X> nonDateWithFormat,
            @NonNull Supplier<Y> dateWithoutFormat,
            @NonNull Supplier<Z> dateWithBadFormat)
            throws W, X, Y, Z
    {
        @SuppressWarnings("unchecked")
        var x = (FormatterFunction<E>) FormatterMap.FORMAT_MAP.get(target);
        if (x != null) {
            if (!format.isEmpty()) throw nonDateWithFormat.get();
            return x;
        }

        @SuppressWarnings("unchecked")
        var d = (Function<DateTimeFormatter, FormatterFunction<E>>) FormatterMap.FORMATTER_DT_MAP.get(target);
        if (d == null) throw unmappeableTarget.get();

        if (format.isEmpty()) throw dateWithoutFormat.get();
        DateTimeFormatter dtf;
        try {
            dtf = DateTimeFormatter.ofPattern(format).withResolverStyle(ResolverStyle.STRICT);
        } catch (IllegalArgumentException e) {
            throw dateWithBadFormat.get();
        }

        return d.apply(dtf);
    }
}