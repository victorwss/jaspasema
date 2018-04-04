package ninja.javahacker.jaspasema.format;

import java.lang.reflect.Parameter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.processor.TargetType;

/**
 * @author Victor Williams Stafusa da Silva
 */
public enum SimpleParameterType {
    SINGULAR, PLURAL, NOT_SIMPLE;

    @SuppressWarnings("unchecked")
    public static SimpleParameterType plural(
            @NonNull Parameter p,
            @NonNull TargetType<?> target)
    {
        ParseFunction<?> pf = ParseFunction.parserFor(target);
        DateTimeParseFunction<?> df = DateTimeParseFunction.parserFor(target);

        @SuppressWarnings("rawtypes")
        ParseFunctionList<?> pfl = ParseFunctionList.parserFor(p, (TargetType) target);

        @SuppressWarnings("rawtypes")
        DateTimeParseFunctionList<?> dfl = DateTimeParseFunctionList.parserFor(p, (TargetType) target);

        if (pf != null || df != null) return SINGULAR;
        if (pfl != null || dfl != null) return PLURAL;

        return NOT_SIMPLE;
    }
}