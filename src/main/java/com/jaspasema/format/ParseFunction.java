package br.gov.sp.prefeitura.smit.cgtic.jaspasema.format;

import br.gov.sp.prefeitura.smit.cgtic.jaspasema.ext.ObjectUtils;
import br.gov.sp.prefeitura.smit.cgtic.jaspasema.processor.TargetType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.function.Function;
import lombok.NonNull;

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
                if ("null".equals(s) || "".equals(s)) return null;
                try {
                    return func.apply(s);
                } catch (Exception e) {
                    throw onError.apply(e)/*"[" + p + "] Invalid value: \"" + s + "\"."*/;
                }
            }
        };
    }

    public static final Map<Class<?>, ParseFunction<?>> MAP = ObjectUtils.makeMap(put -> {
        put.accept(boolean.class, ParseFunction.of(s -> {
            switch (s) {
                case "true": return true;
                case "false": return false;
                default: throw new IllegalArgumentException(s);
            }
        }));
        put.accept(byte.class, ParseFunction.of(Byte::valueOf));
        put.accept(Byte.class, ParseFunction.of(Byte::valueOf));
        put.accept(short.class, ParseFunction.of(Short::valueOf));
        put.accept(Short.class, ParseFunction.of(Short::valueOf));
        put.accept(int.class, ParseFunction.of(Integer::valueOf));
        put.accept(Integer.class, ParseFunction.of(Integer::valueOf));
        put.accept(long.class, ParseFunction.of(Long::valueOf));
        put.accept(Long.class, ParseFunction.of(Long::valueOf));
        put.accept(float.class, ParseFunction.of(Float::valueOf));
        put.accept(Float.class, ParseFunction.of(Float::valueOf));
        put.accept(double.class, ParseFunction.of(Double::valueOf));
        put.accept(Double.class, ParseFunction.of(Double::valueOf));
        put.accept(boolean.class, ParseFunction.of(Boolean::valueOf));
        put.accept(Boolean.class, ParseFunction.of(Boolean::valueOf));
        put.accept(BigInteger.class, ParseFunction.of(BigInteger::new));
        put.accept(BigDecimal.class, ParseFunction.of(BigDecimal::new));
        put.accept(String.class, new ParseFunction<String>() {
            @Override
            public <X extends Throwable> String parse(@NonNull Function<? super Throwable, X> onError, @NonNull String s) {
                return s;
            }
        });
    });

    @SuppressWarnings({"unchecked", "element-type-mismatch"})
    public static <E> ParseFunction<E> parserFor(@NonNull TargetType<E> target) {
        return (ParseFunction<E>) MAP.get(target.getGeneric());
    }
}