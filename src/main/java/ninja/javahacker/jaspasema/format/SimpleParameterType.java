package ninja.javahacker.jaspasema.format;

import java.lang.reflect.Parameter;
import lombok.NonNull;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
public enum SimpleParameterType {
    SINGULAR, PLURAL, NOT_SIMPLE;

    @SuppressWarnings("unchecked")
    public static SimpleParameterType plural(
            @NonNull Parameter p,
            @NonNull ReifiedGeneric<?> target)
    {
        ParseFunction<?> pf = ParseFunction.parserFor(target);
        DateTimeParseFunction<?> df = DateTimeParseFunction.parserFor(target);

        @SuppressWarnings("rawtypes")
        ParseFunctionList<?> pfl = ParseFunctionList.parserFor(p, (ReifiedGeneric) target);

        @SuppressWarnings("rawtypes")
        DateTimeParseFunctionList<?> dfl = DateTimeParseFunctionList.parserFor(p, (ReifiedGeneric) target);

        if (pf != null || df != null) return SINGULAR;
        if (pfl != null || dfl != null) return PLURAL;

        return NOT_SIMPLE;
    }
}