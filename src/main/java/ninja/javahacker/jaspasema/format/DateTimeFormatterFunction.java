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

    public static final Map<Class<?>, DateTimeFormatterFunction<?>> DT_MAP = Map.ofEntries(
            Map.entry(LocalDate.class, of(LocalDate::format)),
            Map.entry(LocalDateTime.class, of(LocalDateTime::format)),
            Map.entry(LocalTime.class, of(LocalTime::format)),
            Map.entry(Year.class, of(Year::format)),
            Map.entry(YearMonth.class, of(YearMonth::format))
    );

    @SuppressWarnings({"unchecked", "element-type-mismatch"})
    public static <E> DateTimeFormatterFunction<E> formatterFor(@NonNull TargetType<E> target) {
        return (DateTimeFormatterFunction<E>) DT_MAP.get(target.getGeneric());
    }
}