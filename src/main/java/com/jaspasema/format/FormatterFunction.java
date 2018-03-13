package br.gov.sp.prefeitura.smit.cgtic.jaspasema.format;

import br.gov.sp.prefeitura.smit.cgtic.jaspasema.ext.ObjectUtils;
import br.gov.sp.prefeitura.smit.cgtic.jaspasema.processor.TargetType;
import java.util.Map;
import java.util.function.Function;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface FormatterFunction<E> {
    public String format(E value);

    public static <E> FormatterFunction<E> of(Function<E, String> func) {
        return v -> v == null ? null : func.apply(v);
    }

    public static final Map<Class<?>, FormatterFunction<?>> MAP = ObjectUtils.makeMap(put -> {
        put.accept(boolean.class, of((Boolean b) -> b.toString()));
        put.accept(Boolean.class, of((Boolean b) -> b.toString()));
        put.accept(byte.class, of((Byte b) -> b.toString()));
        put.accept(Byte.class, of((Byte b) -> b.toString()));
        put.accept(short.class, of((Short b) -> b.toString()));
        put.accept(Short.class, of((Short b) -> b.toString()));
        put.accept(int.class, of((Integer b) -> b.toString()));
        put.accept(Integer.class, of((Integer b) -> b.toString()));
        put.accept(long.class, of((Long b) -> b.toString()));
        put.accept(Long.class, of((Long b) -> b.toString()));
        put.accept(float.class, of((Float b) -> b.toString()));
        put.accept(Float.class, of((Float b) -> b.toString()));
        put.accept(double.class, of((Double b) -> b.toString()));
        put.accept(Double.class, of((Double b) -> b.toString()));
        put.accept(String.class, (String s) -> s);
    });

    @SuppressWarnings({"unchecked", "element-type-mismatch"})
    public static <E> FormatterFunction<E> formatterFor(@NonNull TargetType<E> target) {
        return (FormatterFunction<E>) MAP.get(target.getGeneric());
    }
}