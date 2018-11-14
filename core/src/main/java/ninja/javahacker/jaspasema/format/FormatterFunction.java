package ninja.javahacker.jaspasema.format;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Map;
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
    public String format(E value);

    private static <E> FormatterFunction<E> of(Function<E, String> func) {
        return v -> v == null ? null : func.apply(v);
    }

    public static final Map<ReifiedGeneric<?>, FormatterFunction<?>> MAP = Map.ofEntries(
            Map.entry(ReifiedGeneric.of(boolean.class), of((Boolean b) -> b.toString())),
            Map.entry(ReifiedGeneric.of(Boolean.class), of((Boolean b) -> b.toString())),
            Map.entry(ReifiedGeneric.of(byte.class), of((Byte b) -> b.toString())),
            Map.entry(ReifiedGeneric.of(Byte.class), of((Byte b) -> b.toString())),
            Map.entry(ReifiedGeneric.of(short.class), of((Short b) -> b.toString())),
            Map.entry(ReifiedGeneric.of(Short.class), of((Short b) -> b.toString())),
            Map.entry(ReifiedGeneric.of(int.class), of((Integer b) -> b.toString())),
            Map.entry(ReifiedGeneric.of(Integer.class), of((Integer b) -> b.toString())),
            Map.entry(ReifiedGeneric.of(long.class), of((Long b) -> b.toString())),
            Map.entry(ReifiedGeneric.of(Long.class), of((Long b) -> b.toString())),
            Map.entry(ReifiedGeneric.of(float.class), of((Float b) -> b.toString())),
            Map.entry(ReifiedGeneric.of(Float.class), of((Float b) -> b.toString())),
            Map.entry(ReifiedGeneric.of(double.class), of((Double b) -> b.toString())),
            Map.entry(ReifiedGeneric.of(Double.class), of((Double b) -> b.toString())),
            Map.entry(ReifiedGeneric.of(String.class), of(String::toString))
    );

    public static final Map<ReifiedGeneric<?>, Function<DateTimeFormatter, ? extends FormatterFunction<?>>> DT_MAP = Map.ofEntries(
            Map.entry(ReifiedGeneric.of(LocalDate.class), dtf -> of((LocalDate v) -> v.format(dtf))),
            Map.entry(ReifiedGeneric.of(LocalDateTime.class), dtf -> of((LocalDateTime v) -> v.format(dtf))),
            Map.entry(ReifiedGeneric.of(LocalTime.class), dtf -> of((LocalTime v) -> v.format(dtf))),
            Map.entry(ReifiedGeneric.of(Year.class), dtf -> of((Year v) -> v.format(dtf))),
            Map.entry(ReifiedGeneric.of(YearMonth.class), dtf -> of((YearMonth v) -> v.format(dtf)))
    );

    @SuppressWarnings("unchecked")
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
        FormatterFunction<E> x = (FormatterFunction<E>) MAP.get(target);
        if (x != null) {
            if (!format.isEmpty()) throw nonDateWithFormat.get();
            return x;
        }

        Function<DateTimeFormatter, FormatterFunction<E>> d =
                (Function<DateTimeFormatter, FormatterFunction<E>>) DT_MAP.get(target);
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