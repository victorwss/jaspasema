package ninja.javahacker.jaspasema.format;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.List;
import lombok.Getter;
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
public interface ObjectListParser<E> {
    public List<E> make(List<String> in) throws ParameterValueException;

    public static <E> ObjectListParser<E> prepare(
            @NonNull ReifiedGeneric<List<E>> target,
            @NonNull Class<? extends Annotation> annotationClass,
            @NonNull String format,
            @NonNull Parameter p)
            throws BadServiceMappingException
    {
        ParseFunctionList<E> pf = ParseFunctionList.parserFor(p, target);
        DateTimeParseFunctionList<E> df = DateTimeParseFunctionList.parserFor(p, target);
        if (pf == null && df == null) {
            throw TypeRestrictionViolationException.create(
                    p,
                    annotationClass,
                    TypeRestrictionViolationException.AllowedTypes.SIMPLE_LIST,
                    target);
        }
        if (pf != null) {
            if (!format.isEmpty()) {
                throw TypeRestrictionViolationException.create(
                        p,
                        annotationClass,
                        TypeRestrictionViolationException.AllowedTypes.DATE_TIME_LIST,
                        target);
            }
            return list ->
                    pf.parse(
                            a -> ParameterValueException.MalformedParameterException.create(p, annotationClass, String.valueOf(list), a),
                            list);
        }
        DateTimeFormatter dtf;
        try {
            dtf = DateTimeFormatter.ofPattern(format).withResolverStyle(ResolverStyle.STRICT);
        } catch (IllegalArgumentException e) {
            throw InvalidDateFormatException.create(p, annotationClass, format);
        }
        return list ->
                df.parse(
                        a -> ParameterValueException.MalformedParameterException.create(p, annotationClass, String.valueOf(list), a),
                        list,
                        dtf);
    }
}