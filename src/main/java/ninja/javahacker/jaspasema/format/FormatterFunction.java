package ninja.javahacker.jaspasema.format;

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
public interface FormatterFunction<E> {
    public String format(E value);

    public static <E> FormatterFunction<E> of(Function<E, String> func) {
        return v -> v == null ? null : func.apply(v);
    }

    public static final Map<ReifiedGeneric<?>, FormatterFunction<?>> MAP = Map.ofEntries(
            Map.entry(ReifiedGeneric.forClass(boolean.class), of((Boolean b) -> b.toString())),
            Map.entry(ReifiedGeneric.forClass(Boolean.class), of((Boolean b) -> b.toString())),
            Map.entry(ReifiedGeneric.forClass(byte.class), of((Byte b) -> b.toString())),
            Map.entry(ReifiedGeneric.forClass(Byte.class), of((Byte b) -> b.toString())),
            Map.entry(ReifiedGeneric.forClass(short.class), of((Short b) -> b.toString())),
            Map.entry(ReifiedGeneric.forClass(Short.class), of((Short b) -> b.toString())),
            Map.entry(ReifiedGeneric.forClass(int.class), of((Integer b) -> b.toString())),
            Map.entry(ReifiedGeneric.forClass(Integer.class), of((Integer b) -> b.toString())),
            Map.entry(ReifiedGeneric.forClass(long.class), of((Long b) -> b.toString())),
            Map.entry(ReifiedGeneric.forClass(Long.class), of((Long b) -> b.toString())),
            Map.entry(ReifiedGeneric.forClass(float.class), of((Float b) -> b.toString())),
            Map.entry(ReifiedGeneric.forClass(Float.class), of((Float b) -> b.toString())),
            Map.entry(ReifiedGeneric.forClass(double.class), of((Double b) -> b.toString())),
            Map.entry(ReifiedGeneric.forClass(Double.class), of((Double b) -> b.toString())),
            Map.entry(ReifiedGeneric.forClass(String.class), of((String s) -> s))
    );

    public static final Map<ReifiedGeneric<?>, Function<DateTimeFormatter, ? extends FormatterFunction<?>>> DT_MAP = Map.ofEntries(
            Map.entry(ReifiedGeneric.forClass(LocalDate.class), dtf -> of((LocalDate v) -> v.format(dtf))),
            Map.entry(ReifiedGeneric.forClass(LocalDateTime.class), dtf -> of((LocalDateTime v) -> v.format(dtf))),
            Map.entry(ReifiedGeneric.forClass(LocalTime.class), dtf -> of((LocalTime v) -> v.format(dtf))),
            Map.entry(ReifiedGeneric.forClass(Year.class), dtf -> of((Year v) -> v.format(dtf))),
            Map.entry(ReifiedGeneric.forClass(YearMonth.class), dtf -> of((YearMonth v) -> v.format(dtf)))
    );

    @SuppressWarnings("unchecked")
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