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
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.NonNull;
import lombok.experimental.PackagePrivate;
import lombok.experimental.UtilityClass;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
@UtilityClass
@PackagePrivate
@SuppressFBWarnings("FII_USE_METHOD_REFERENCE")
class FormatterMap {

    public static final Map<ReifiedGeneric<?>, FormatterFunction<?>> FORMAT_MAP = Map.ofEntries(
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
            Map.entry(ReifiedGeneric.of(BigInteger.class), of((BigInteger b) -> b.toString())),
            Map.entry(ReifiedGeneric.of(BigDecimal.class), of((BigDecimal b) -> b.toPlainString())),
            Map.entry(ReifiedGeneric.of(String.class), of((String b) -> b))
    );

    public static final Map<ReifiedGeneric<?>, Function<DateTimeFormatter, ? extends FormatterFunction<?>>> FORMATTER_DT_MAP
            = Map.ofEntries(
                    Map.entry(ReifiedGeneric.of(LocalDate.class), dtf -> of((LocalDate v) -> v.format(dtf))),
                    Map.entry(ReifiedGeneric.of(LocalDateTime.class), dtf -> of((LocalDateTime v) -> v.format(dtf))),
                    Map.entry(ReifiedGeneric.of(LocalTime.class), dtf -> of((LocalTime v) -> v.format(dtf))),
                    Map.entry(ReifiedGeneric.of(Year.class), dtf -> of((Year v) -> v.format(dtf))),
                    Map.entry(ReifiedGeneric.of(YearMonth.class), dtf -> of((YearMonth v) -> v.format(dtf)))
            );

    public static final Map<ReifiedGeneric<?>, ParseFunction<?>> PARSE_MAP = Map.ofEntries(
            Map.entry(ReifiedGeneric.of(boolean.class), parse(FormatterMap::booleanParse)),
            Map.entry(ReifiedGeneric.of(Boolean.class), parse(FormatterMap::booleanParse)),
            Map.entry(ReifiedGeneric.of(byte.class), parse(Byte::valueOf)),
            Map.entry(ReifiedGeneric.of(Byte.class), parse(Byte::valueOf)),
            Map.entry(ReifiedGeneric.of(short.class), parse(Short::valueOf)),
            Map.entry(ReifiedGeneric.of(Short.class), parse(Short::valueOf)),
            Map.entry(ReifiedGeneric.of(int.class), parse(Integer::valueOf)),
            Map.entry(ReifiedGeneric.of(Integer.class), parse(Integer::valueOf)),
            Map.entry(ReifiedGeneric.of(long.class), parse(Long::valueOf)),
            Map.entry(ReifiedGeneric.of(Long.class), parse(Long::valueOf)),
            Map.entry(ReifiedGeneric.of(float.class), parse(Float::valueOf)),
            Map.entry(ReifiedGeneric.of(Float.class), parse(Float::valueOf)),
            Map.entry(ReifiedGeneric.of(double.class), parse(Double::valueOf)),
            Map.entry(ReifiedGeneric.of(Double.class), parse(Double::valueOf)),
            Map.entry(ReifiedGeneric.of(BigInteger.class), parse(BigInteger::new)),
            Map.entry(ReifiedGeneric.of(BigDecimal.class), parse(BigDecimal::new)),
            Map.entry(ReifiedGeneric.of(String.class), new ParseFunction<String>() {
                @Override
                public <X extends Throwable> String parse(@NonNull Function<? super Throwable, X> onError, @NonNull String s) {
                    return s;
                }
            })
    );

    public static final Map<ReifiedGeneric<?>, Function<DateTimeFormatter, ? extends ParseFunction<?>>> PARSE_DT_MAP = Map.ofEntries(
            Map.entry(ReifiedGeneric.of(LocalDate.class), parseDate(LocalDate::parse)),
            Map.entry(ReifiedGeneric.of(LocalDateTime.class), parseDate(LocalDateTime::parse)),
            Map.entry(ReifiedGeneric.of(LocalTime.class), parseDate(LocalTime::parse)),
            Map.entry(ReifiedGeneric.of(Year.class), parseDate(Year::parse)),
            Map.entry(ReifiedGeneric.of(YearMonth.class), parseDate(YearMonth::parse))
    );

    @NonNull
    private static <E> FormatterFunction<E> of(@NonNull Function<E, String> func) {
        return v -> v == null ? null : func.apply(v);
    }

    private static boolean booleanParse(@NonNull String s) {
        switch (s) {
            case "true":
                return true;
            case "false":
                return false;
            default:
                throw new IllegalArgumentException(s);
        }
    }

    @NonNull
    private static <E> ParseFunction<E> parse(@NonNull Function<String, E> func) {
        return new ParseFunction<>() {
            @Nullable
            @Override
            @SuppressFBWarnings("LEST_LOST_EXCEPTION_STACK_TRACE")
            public <X extends Throwable> E parse(@NonNull Function<? super Throwable, X> onError, @NonNull String s) throws X {
                if ("null".equals(s) || s.isEmpty()) {
                    return null;
                }
                try {
                    return func.apply(s);
                } catch (Exception e) {
                    throw onError.apply(e);
                }
            }
        };
    }

    private static <E> Function<DateTimeFormatter, ParseFunction<E>> parseDate(@NonNull BiFunction<String, DateTimeFormatter, E> func) {
        return format -> new ParseFunction<>() {
            @Nullable
            @Override
            @SuppressFBWarnings("LEST_LOST_EXCEPTION_STACK_TRACE")
            public <X extends Throwable> E parse(@NonNull Function<? super Throwable, X> onError, @NonNull String s) throws X {
                if ("null".equals(s) || s.isEmpty()) {
                    return null;
                }
                try {
                    return func.apply(s, format);
                } catch (DateTimeException e) {
                    throw onError.apply(e);
                }
            }
        };
    }
}
