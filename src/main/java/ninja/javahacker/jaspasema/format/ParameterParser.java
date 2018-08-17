package ninja.javahacker.jaspasema.format;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.InvalidDateFormatException;
import ninja.javahacker.jaspasema.exceptions.ParameterValueException;
import ninja.javahacker.jaspasema.exceptions.TypeRestrictionViolationException;
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
        String annotationName = annotationClass.getSimpleName();
        ParseFunction<E> pf = ParseFunction.parserFor(target);
        DateTimeParseFunction<E> df = DateTimeParseFunction.parserFor(target);
        if (pf == null && df == null) {
            throw TypeRestrictionViolationException.create(
                    p,
                    annotationClass,
                    TypeRestrictionViolationException.AllowedTypes.SIMPLE,
                    target);
        }
        if (pf != null) {
            if (!format.isEmpty()) {
                throw TypeRestrictionViolationException.create(
                    p,
                    annotationClass,
                    TypeRestrictionViolationException.AllowedTypes.DATE_TIME,
                    target);
            }
            return body ->
                    pf.parse(
                            a -> ParameterValueException.MalformedParameterException.create(p, annotationClass, body, a),
                            body);
        }
        DateTimeFormatter dtf;
        try {
            dtf = DateTimeFormatter.ofPattern(format).withResolverStyle(ResolverStyle.STRICT);
        } catch (IllegalArgumentException e) {
            throw InvalidDateFormatException.create(p, annotationClass, format);
        }

        return body ->
                df.parse(
                        a -> ParameterValueException.MalformedParameterException.create(p, annotationClass, body, a),
                        body,
                        dtf);
    }
}