package ninja.javahacker.jaspasema.format;

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
import ninja.javahacker.jaspasema.ext.ObjectUtils;
import ninja.javahacker.jaspasema.processor.TargetType;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface DateTimeParseFunction<E> {
    public <X extends Throwable> E parse(Function<? super Throwable, X> onError, String s, DateTimeFormatter format) throws X;

    public static <E> DateTimeParseFunction<E> of(BiFunction<String, DateTimeFormatter, E> func) {
        return new DateTimeParseFunction<E>() {
            @Override
            public <X extends Throwable> E parse(Function<? super Throwable, X> onError, String s, DateTimeFormatter format) throws X {
                if ("null".equals(s) || "".equals(s)) return null;
                try {
                    return func.apply(s, format);
                } catch (DateTimeException e) {
                    throw onError.apply(e);
                }
            }
        };
    }

    public static final Map<Class<?>, DateTimeParseFunction<?>> DT_MAP = ObjectUtils.makeMap(put -> {
        put.accept(LocalDate.class, DateTimeParseFunction.of(LocalDate::parse));
        put.accept(LocalDateTime.class, DateTimeParseFunction.of(LocalDateTime::parse));
        put.accept(LocalTime.class, DateTimeParseFunction.of(LocalTime::parse));
        put.accept(Year.class, DateTimeParseFunction.of(Year::parse));
        put.accept(YearMonth.class, DateTimeParseFunction.of(YearMonth::parse));
    });

    @SuppressWarnings({"unchecked", "element-type-mismatch"})
    public static <E> DateTimeParseFunction<E> parserFor(@NonNull TargetType<E> target) {
        return (DateTimeParseFunction<E>) DT_MAP.get(target.getGeneric());
    }
}