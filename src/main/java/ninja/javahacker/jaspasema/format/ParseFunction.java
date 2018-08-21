package ninja.javahacker.jaspasema.format;

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
    public <X extends Throwable> E parse(Function<? super Throwable, X> onError, String s) throws X;

    public static <E> ParseFunction<E> of(@NonNull Function<String, E> func) {
        return new ParseFunction<E>() {
            @Override
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

    public static <E> Function<DateTimeFormatter, ParseFunction<E>> ofDate(BiFunction<String, DateTimeFormatter, E> func) {
        return format -> new ParseFunction<E>() {
            @Override
            public <X extends Throwable> E parse(Function<? super Throwable, X> onError, String s) throws X {
                if ("null".equals(s) || s.isEmpty()) return null;
                try {
                    return func.apply(s, format);
                } catch (DateTimeException e) {
                    throw onError.apply(e);
                }
            }
        };
    }

    private static boolean booleanParse(String s) {
        switch (s) {
            case "true": return true;
            case "false": return false;
            default: throw new IllegalArgumentException(s);
        }
    }

    public static final Map<ReifiedGeneric<?>, ParseFunction<?>> MAP = Map.ofEntries(
            Map.entry(ReifiedGeneric.forClass(boolean.class), ParseFunction.of(ParseFunction::booleanParse)),
            Map.entry(ReifiedGeneric.forClass(Boolean.class), ParseFunction.of(ParseFunction::booleanParse)),
            Map.entry(ReifiedGeneric.forClass(byte.class), ParseFunction.of(Byte::valueOf)),
            Map.entry(ReifiedGeneric.forClass(Byte.class), ParseFunction.of(Byte::valueOf)),
            Map.entry(ReifiedGeneric.forClass(short.class), ParseFunction.of(Short::valueOf)),
            Map.entry(ReifiedGeneric.forClass(Short.class), ParseFunction.of(Short::valueOf)),
            Map.entry(ReifiedGeneric.forClass(int.class), ParseFunction.of(Integer::valueOf)),
            Map.entry(ReifiedGeneric.forClass(Integer.class), ParseFunction.of(Integer::valueOf)),
            Map.entry(ReifiedGeneric.forClass(long.class), ParseFunction.of(Long::valueOf)),
            Map.entry(ReifiedGeneric.forClass(Long.class), ParseFunction.of(Long::valueOf)),
            Map.entry(ReifiedGeneric.forClass(float.class), ParseFunction.of(Float::valueOf)),
            Map.entry(ReifiedGeneric.forClass(Float.class), ParseFunction.of(Float::valueOf)),
            Map.entry(ReifiedGeneric.forClass(double.class), ParseFunction.of(Double::valueOf)),
            Map.entry(ReifiedGeneric.forClass(Double.class), ParseFunction.of(Double::valueOf)),
            Map.entry(ReifiedGeneric.forClass(BigInteger.class), ParseFunction.of(BigInteger::new)),
            Map.entry(ReifiedGeneric.forClass(BigDecimal.class), ParseFunction.of(BigDecimal::new)),
            Map.entry(ReifiedGeneric.forClass(String.class), new ParseFunction<String>() {
                @Override
                public <X extends Throwable> String parse(@NonNull Function<? super Throwable, X> onError, @NonNull String s) {
                    return s;
                }
            })
    );

    public static final Map<ReifiedGeneric<?>, Function<DateTimeFormatter, ? extends ParseFunction<?>>> DT_MAP = Map.ofEntries(
            Map.entry(ReifiedGeneric.forClass(LocalDate.class), ofDate(LocalDate::parse)),
            Map.entry(ReifiedGeneric.forClass(LocalDateTime.class), ofDate(LocalDateTime::parse)),
            Map.entry(ReifiedGeneric.forClass(LocalTime.class), ofDate(LocalTime::parse)),
            Map.entry(ReifiedGeneric.forClass(Year.class), ofDate(Year::parse)),
            Map.entry(ReifiedGeneric.forClass(YearMonth.class), ofDate(YearMonth::parse))
    );

    public static boolean accepts(@NonNull ReifiedGeneric<?> target) {
        return MAP.get(target) != null || DT_MAP.get(target) != null;
    }

    @SuppressWarnings("unchecked")
    public static <E, W extends Throwable, X extends Throwable, Y extends Throwable, Z extends Throwable> ParseFunction<E> parserFor(
            @NonNull String format,
            @NonNull ReifiedGeneric<E> target,
            @NonNull Supplier<W> unmappeableTarget,
            @NonNull Supplier<X> nonDateWithFormat,
            @NonNull Supplier<Y> dateWithoutFormat,
            @NonNull Supplier<Z> dateWithBadFormat)
            throws W, X, Y, Z
    {
        ParseFunction<E> x = (ParseFunction<E>) MAP.get(target);
        if (x != null) {
            if (!format.isEmpty()) throw nonDateWithFormat.get();
            return x;
        }

        Function<DateTimeFormatter, ParseFunction<E>> d = (Function<DateTimeFormatter, ParseFunction<E>>) DT_MAP.get(target);
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