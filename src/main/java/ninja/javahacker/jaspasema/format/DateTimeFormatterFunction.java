package ninja.javahacker.jaspasema.format;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.BiFunction;
import lombok.NonNull;
import ninja.javahacker.jaspasema.ext.ObjectUtils;
import ninja.javahacker.jaspasema.processor.TargetType;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface DateTimeFormatterFunction<E> {
    public String format(E value, DateTimeFormatter format);

    public static <E> DateTimeFormatterFunction<E> of(BiFunction<E, DateTimeFormatter, String> func) {
        return (v, f) -> v == null ? null : func.apply(v, f);
    }

    public static final Map<Class<?>, DateTimeFormatterFunction<?>> DT_MAP = ObjectUtils.makeMap(put -> {
        put.accept(LocalDate.class, of(LocalDate::format));
        put.accept(LocalDateTime.class, of(LocalDateTime::format));
        put.accept(LocalTime.class, of(LocalTime::format));
        put.accept(Year.class, of(Year::format));
        put.accept(YearMonth.class, of(YearMonth::format));
    });

    @SuppressWarnings({"unchecked", "element-type-mismatch"})
    public static <E> DateTimeFormatterFunction<E> formatterFor(@NonNull TargetType<E> target) {
        return (DateTimeFormatterFunction<E>) DT_MAP.get(target.getGeneric());
    }
}