package ninja.javahacker.jaspasema.format;

import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.function.Supplier;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.AllowedTypes;
import ninja.javahacker.jaspasema.exceptions.badmapping.EmptyDateFormatException;
import ninja.javahacker.jaspasema.exceptions.badmapping.InvalidDateFormatException;
import ninja.javahacker.jaspasema.exceptions.badmapping.ParameterTypeRestrictionViolationException;
import ninja.javahacker.jaspasema.exceptions.paramvalue.MalformedParameterValueException;
import ninja.javahacker.jaspasema.exceptions.paramvalue.ParameterValueException;
import ninja.javahacker.jaspasema.processor.AnnotatedParameter;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface ParameterParser<E> {
    @Nullable
    public E make(@Nullable String in) throws ParameterValueException;

    @NonNull
    public static <E> ParameterParser<E> prepare(@NonNull AnnotatedParameter<?, E> param, @NonNull String dateFormat)
            throws ParameterTypeRestrictionViolationException, EmptyDateFormatException, InvalidDateFormatException
    {
        var w = ParameterTypeRestrictionViolationException.getFor(param, AllowedTypes.SIMPLE);
        var x = ParameterTypeRestrictionViolationException.getFor(param, AllowedTypes.DATE_TIME);
        var p = param.getParameter();
        var annotationClass = param.getAnnotationType();

        Supplier<EmptyDateFormatException> y = () -> new EmptyDateFormatException(p, annotationClass);
        Supplier<InvalidDateFormatException> z = () -> new InvalidDateFormatException(p, annotationClass, dateFormat);

        var pf = ParseFunction.parserFor(dateFormat, param.getTarget(), w, x, y, z);

        return body -> {
            var m = MalformedParameterValueException.expectingCause(param, body);
            return pf.parse(m, body);
        };
    }
}