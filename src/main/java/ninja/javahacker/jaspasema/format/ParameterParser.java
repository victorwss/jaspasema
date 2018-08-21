package ninja.javahacker.jaspasema.format;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.function.Supplier;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.badmapping.EmptyDateFormatException;
import ninja.javahacker.jaspasema.exceptions.badmapping.InvalidDateFormatException;
import ninja.javahacker.jaspasema.exceptions.badmapping.TypeRestrictionViolationException;
import ninja.javahacker.jaspasema.exceptions.paramvalue.MalformedParameterValueException;
import ninja.javahacker.jaspasema.exceptions.paramvalue.ParameterValueException;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface ParameterParser<E> {
    public E make(String in) throws ParameterValueException;

    public static <E> ParameterParser<E> prepare(
            @NonNull ReifiedGeneric<E> target,
            @NonNull Class<? extends Annotation> annotationClass,
            @NonNull String format,
            @NonNull Parameter p)
            throws BadServiceMappingException
    {
        Supplier<TypeRestrictionViolationException> w = () -> TypeRestrictionViolationException.create(
                    p,
                    annotationClass,
                    TypeRestrictionViolationException.AllowedTypes.SIMPLE,
                    target);

        Supplier<TypeRestrictionViolationException> x = () -> TypeRestrictionViolationException.create(
                    p,
                    annotationClass,
                    TypeRestrictionViolationException.AllowedTypes.DATE_TIME,
                    target);

        Supplier<EmptyDateFormatException> y = () -> EmptyDateFormatException.create(p, annotationClass);
        Supplier<InvalidDateFormatException> z = () -> InvalidDateFormatException.create(p, annotationClass, format);

        ParseFunction<E> pf = ParseFunction.parserFor(format, target, w, x, y, z);

        return body ->
                pf.parse(
                        a -> MalformedParameterValueException.create(p, annotationClass, body, a),
                        body);
    }
}