package ninja.javahacker.jaspasema.format;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import lombok.NonNull;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface ParseFunctionList<E> {
    public <X extends Throwable> List<E> parse(Function<? super Throwable, X> onError, List<String> list) throws X;

    @SuppressWarnings({"unchecked", "element-type-mismatch"})
    public static <E> ParseFunctionList<E> parserFor(
            @NonNull Parameter p,
            @NonNull ReifiedGeneric<List<E>> target)
    {
        if (!target.isAssignableFrom(List.class)) return null;
        ParseFunction<E> func = ParseFunction.parserFor(ReifiedGeneric.unwrapIterableGenericType(target));
        return new ParseFunctionList<E>() {
            @Override
            public <X extends Throwable> List<E> parse(Function<? super Throwable, X> onError, List<String> list) throws X {
                List<E> result = new ArrayList<>(list.size());
                for (String s : list) {
                    result.add(func.parse(onError, s));
                }
                return result;
            }
        };
    }
}
