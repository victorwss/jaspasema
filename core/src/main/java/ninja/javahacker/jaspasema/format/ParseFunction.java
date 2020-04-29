package ninja.javahacker.jaspasema.format;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Map;
import java.util.function.BiFunction;
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

    @NonNull
    private static <E> ParseFunction<E> of(@NonNull Function<String, E> func) {
        return new ParseFunction<>() {
            @Nullable
            @Override
            @SuppressFBWarnings("LEST_LOST_EXCEPTION_STACK_TRACE")
            public <X extends Throwable> E parse(@NonNull Function<? super Throwable, X> onError, @NonNull String s) throws X {
                if ("null".equals(s) || s.isEmpty()) return null;
                try {
                    return func.apply(s);
                } catch (Exception e) {
                    throw onError.apply(e);
                }
            }
        };
    }

    private static <E> Function<DateTimeFormatter, ParseFunction<E>> ofDate(@NonNull BiFunction<String, DateTimeFormatter, E> func) {
        return format -> new ParseFunction<>() {
            @Nullable
            @Override
            @SuppressFBWarnings("LEST_LOST_EXCEPTION_STACK_TRACE")
            public <X extends Throwable> E parse(@NonNull Function<? super Throwable, X> onError, @NonNull String s) throws X {
                if ("null".equals(s) || s.isEmpty()) return null;
                try {
                    return func.apply(s, format);
                } catch (DateTimeException e) {
                    throw onError.apply(e);
                }
            }
        };
    }

    private static boolean booleanParse(@NonNull String s) {
        switch (s) {
            case "true": return true;
            case "false": return false;
            default: throw new IllegalArgumentException(s);
        }
    }

    public static final Map<ReifiedGeneric<?>, ParseFunction<?>> MAP = Map.ofEntries(
            Map.entry(ReifiedGeneric.of(boolean.class), ParseFunction.of(ParseFunction::booleanParse)),
            Map.entry(ReifiedGeneric.of(Boolean.class), ParseFunction.of(ParseFunction::booleanParse)),
            Map.entry(ReifiedGeneric.of(byte.class), ParseFunction.of(Byte::valueOf)),
            Map.entry(ReifiedGeneric.of(Byte.class), ParseFunction.of(Byte::valueOf)),
            Map.entry(ReifiedGeneric.of(short.class), ParseFunction.of(Short::valueOf)),
            Map.entry(ReifiedGeneric.of(Short.class), ParseFunction.of(Short::valueOf)),
            Map.entry(ReifiedGeneric.of(int.class), ParseFunction.of(Integer::valueOf)),
            Map.entry(ReifiedGeneric.of(Integer.class), ParseFunction.of(Integer::valueOf)),
            Map.entry(ReifiedGeneric.of(long.class), ParseFunction.of(Long::valueOf)),
            Map.entry(ReifiedGeneric.of(Long.class), ParseFunction.of(Long::valueOf)),
            Map.entry(ReifiedGeneric.of(float.class), ParseFunction.of(Float::valueOf)),
            Map.entry(ReifiedGeneric.of(Float.class), ParseFunction.of(Float::valueOf)),
            Map.entry(ReifiedGeneric.of(double.class), ParseFunction.of(Double::valueOf)),
            Map.entry(ReifiedGeneric.of(Double.class), ParseFunction.of(Double::valueOf)),
            Map.entry(ReifiedGeneric.of(BigInteger.class), ParseFunction.of(BigInteger::new)),
            Map.entry(ReifiedGeneric.of(BigDecimal.class), ParseFunction.of(BigDecimal::new)),
            Map.entry(ReifiedGeneric.of(String.class), new ParseFunction<String>() {
                @Override
                public <X extends Throwable> String parse(@NonNull Function<? super Throwable, X> onError, @NonNull String s) {
                    return s;
                }
            })
    );

    public static final Map<ReifiedGeneric<?>, Function<DateTimeFormatter, ? extends ParseFunction<?>>> DT_MAP = Map.ofEntries(
            Map.entry(ReifiedGeneric.of(LocalDate.class), ofDate(LocalDate::parse)),
            Map.entry(ReifiedGeneric.of(LocalDateTime.class), ofDate(LocalDateTime::parse)),
            Map.entry(ReifiedGeneric.of(LocalTime.class), ofDate(LocalTime::parse)),
            Map.entry(ReifiedGeneric.of(Year.class), ofDate(Year::parse)),
            Map.entry(ReifiedGeneric.of(YearMonth.class), ofDate(YearMonth::parse))
    );

    public static boolean accepts(@NonNull ReifiedGeneric<?> target) {
        return MAP.get(target) != null || DT_MAP.get(target) != null;
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
        var x = (ParseFunction<E>) MAP.get(target);
        if (x != null) {
            if (!dateFormat.isEmpty()) throw nonDateWithFormat.get();
            return x;
        }

        @SuppressWarnings("unchecked")
        var d = (Function<DateTimeFormatter, ParseFunction<E>>) DT_MAP.get(target);
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