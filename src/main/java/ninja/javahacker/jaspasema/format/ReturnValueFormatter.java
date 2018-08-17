package ninja.javahacker.jaspasema.format;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Optional;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.InvalidDateFormatException;
import ninja.javahacker.jaspasema.exceptions.TypeRestrictionViolationException;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface ReturnValueFormatter<E> {
    public String make(E in);

    public static <E> ReturnValueFormatter<E> prepare(
            @NonNull ReifiedGeneric<E> target,
            @NonNull Class<? extends Annotation> annotationClass,
            @NonNull String format,
            @NonNull Method method)
            throws BadServiceMappingException
    {
        Optional<FormatterFunction<E>> pf = FormatterFunction.formatterFor(target);
        Optional<DateTimeFormatterFunction<E>> df = DateTimeFormatterFunction.formatterFor(target);
        if (!pf.isPresent() && !df.isPresent()) {
            throw TypeRestrictionViolationException.create(
                    method,
                    annotationClass,
                    TypeRestrictionViolationException.AllowedTypes.SIMPLE,
                    target);
        }
        if (pf.isPresent()) {
            if (!format.isEmpty()) {
                throw TypeRestrictionViolationException.create(
                        method,
                        annotationClass,
                        TypeRestrictionViolationException.AllowedTypes.DATE_TIME,
                        target);
            }
            return pf.get()::format;
        }
        DateTimeFormatter dtf;
        try {
            dtf = DateTimeFormatter.ofPattern(format).withResolverStyle(ResolverStyle.STRICT);
        } catch (IllegalArgumentException e) {
            throw InvalidDateFormatException.create(method, annotationClass, format);
        }
        return body -> df.get().format(body, dtf);
    }
}