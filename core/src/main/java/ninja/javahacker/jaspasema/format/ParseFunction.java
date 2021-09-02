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
public interface ParseFunction<E> {
    @Nullable
    public <X extends Throwable> E parse(@NonNull Function<? super Throwable, X> onError, @NonNull String s) throws X;

    public static boolean accepts(@NonNull ReifiedGeneric<?> target) {
        return FormatterMap.PARSE_MAP.get(target) != null || FormatterMap.PARSE_DT_MAP.get(target) != null;
    }

    @NonNull
    @SuppressFBWarnings({"LEST_LOST_EXCEPTION_STACK_TRACE", "BED_HIERARCHICAL_EXCEPTION_DECLARATION"})
    public static <E, W extends Throwable, X extends Throwable, Y extends Throwable, Z extends Throwable> ParseFunction<E> parserFor(
            @NonNull String dateFormat,
            @NonNull ReifiedGeneric<E> target,
            @NonNull Supplier<W> unmappeableTarget,
            @NonNull Supplier<X> nonDateWithFormat,
            @NonNull Supplier<Y> dateWithoutFormat,
            @NonNull Supplier<Z> dateWithBadFormat)
            throws W, X, Y, Z
    {
        @SuppressWarnings("unchecked")
        var x = (ParseFunction<E>) FormatterMap.PARSE_MAP.get(target);
        if (x != null) {
            if (!dateFormat.isEmpty()) throw nonDateWithFormat.get();
            return x;
        }

        @SuppressWarnings("unchecked")
        var d = (Function<DateTimeFormatter, ParseFunction<E>>) FormatterMap.PARSE_DT_MAP.get(target);
        if (d == null) throw unmappeableTarget.get();

        if (dateFormat.isEmpty()) throw dateWithoutFormat.get();
        DateTimeFormatter dtf;
        try {
            dtf = DateTimeFormatter.ofPattern(dateFormat).withResolverStyle(ResolverStyle.STRICT);
        } catch (IllegalArgumentException e) {
            throw dateWithBadFormat.get();
        }

        return d.apply(dtf);
    }
}