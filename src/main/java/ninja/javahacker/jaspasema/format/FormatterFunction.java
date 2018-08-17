package ninja.javahacker.jaspasema.format;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
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

    public static final Map<Class<?>, FormatterFunction<?>> MAP = Map.ofEntries(
            Map.entry(boolean.class, of((Boolean b) -> b.toString())),
            Map.entry(Boolean.class, of((Boolean b) -> b.toString())),
            Map.entry(byte.class, of((Byte b) -> b.toString())),
            Map.entry(Byte.class, of((Byte b) -> b.toString())),
            Map.entry(short.class, of((Short b) -> b.toString())),
            Map.entry(Short.class, of((Short b) -> b.toString())),
            Map.entry(int.class, of((Integer b) -> b.toString())),
            Map.entry(Integer.class, of((Integer b) -> b.toString())),
            Map.entry(long.class, of((Long b) -> b.toString())),
            Map.entry(Long.class, of((Long b) -> b.toString())),
            Map.entry(float.class, of((Float b) -> b.toString())),
            Map.entry(Float.class, of((Float b) -> b.toString())),
            Map.entry(double.class, of((Double b) -> b.toString())),
            Map.entry(Double.class, of((Double b) -> b.toString())),
            Map.entry(String.class, of((String s) -> s))
    );

    @SuppressWarnings({"unchecked", "element-type-mismatch"})
    public static <E> Optional<FormatterFunction<E>> formatterFor(@NonNull ReifiedGeneric<E> target) {
        return Optional.ofNullable((FormatterFunction<E>) MAP.get(target.getGeneric()));
    }
}