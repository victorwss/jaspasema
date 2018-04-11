package ninja.javahacker.jaspasema.format;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.function.Function;
import lombok.NonNull;
import ninja.javahacker.jaspasema.processor.TargetType;

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

    private static boolean booleanParse(String s) {
        switch (s) {
            case "true": return true;
            case "false": return false;
            default: throw new IllegalArgumentException(s);
        }
    }

    public static final Map<Class<?>, ParseFunction<?>> MAP = Map.ofEntries(
            Map.entry(boolean.class, ParseFunction.of(ParseFunction::booleanParse)),
            Map.entry(Boolean.class, ParseFunction.of(ParseFunction::booleanParse)),
            Map.entry(byte.class, ParseFunction.of(Byte::valueOf)),
            Map.entry(Byte.class, ParseFunction.of(Byte::valueOf)),
            Map.entry(short.class, ParseFunction.of(Short::valueOf)),
            Map.entry(Short.class, ParseFunction.of(Short::valueOf)),
            Map.entry(int.class, ParseFunction.of(Integer::valueOf)),
            Map.entry(Integer.class, ParseFunction.of(Integer::valueOf)),
            Map.entry(long.class, ParseFunction.of(Long::valueOf)),
            Map.entry(Long.class, ParseFunction.of(Long::valueOf)),
            Map.entry(float.class, ParseFunction.of(Float::valueOf)),
            Map.entry(Float.class, ParseFunction.of(Float::valueOf)),
            Map.entry(double.class, ParseFunction.of(Double::valueOf)),
            Map.entry(Double.class, ParseFunction.of(Double::valueOf)),
            Map.entry(BigInteger.class, ParseFunction.of(BigInteger::new)),
            Map.entry(BigDecimal.class, ParseFunction.of(BigDecimal::new)),
            Map.entry(String.class, new ParseFunction<String>() {
                @Override
                public <X extends Throwable> String parse(@NonNull Function<? super Throwable, X> onError, @NonNull String s) {
                    return s;
                }
            })
    );

    @SuppressWarnings({"unchecked", "element-type-mismatch"})
    public static <E> ParseFunction<E> parserFor(@NonNull TargetType<E> target) {
        return (ParseFunction<E>) MAP.get(target.getGeneric());
    }
}