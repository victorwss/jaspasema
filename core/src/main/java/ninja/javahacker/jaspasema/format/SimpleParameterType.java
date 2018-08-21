package ninja.javahacker.jaspasema.format;

import java.lang.reflect.Parameter;
import java.util.List;
import lombok.NonNull;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;
import ninja.javahacker.reifiedgeneric.Wrappers;

/**
 * @author Victor Williams Stafusa da Silva
 */
public enum SimpleParameterType {
    SINGULAR, PLURAL, NOT_SIMPLE;

    @SuppressWarnings("unchecked")
    public static <E> SimpleParameterType plural(
            @NonNull Parameter p,
            @NonNull ReifiedGeneric<E> target)
    {
        if (!target.isCompatibleWith(List.class)) return ParseFunction.accepts(target) ? SINGULAR : NOT_SIMPLE;
        return ParseFunction.accepts(Wrappers.unwrapIterable((ReifiedGeneric<List<E>>) target)) ? PLURAL : NOT_SIMPLE;
    }
}