package ninja.javahacker.jaspasema.format;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.function.Supplier;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.badmapping.EmptyDateFormatException;
import ninja.javahacker.jaspasema.exceptions.badmapping.InvalidDateFormatException;
import ninja.javahacker.jaspasema.exceptions.badmapping.TypeRestrictionViolationException;
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
        Supplier<TypeRestrictionViolationException> w = () -> TypeRestrictionViolationException.create(
                    method,
                    annotationClass,
                    TypeRestrictionViolationException.AllowedTypes.SIMPLE,
                    target);

        Supplier<TypeRestrictionViolationException> x = () -> TypeRestrictionViolationException.create(
                    method,
                    annotationClass,
                    TypeRestrictionViolationException.AllowedTypes.DATE_TIME,
                    target);

        Supplier<EmptyDateFormatException> y = () -> EmptyDateFormatException.create(method, annotationClass);
        Supplier<InvalidDateFormatException> z = () -> InvalidDateFormatException.create(method, annotationClass, format);

        FormatterFunction<E> pf = FormatterFunction.formatterFor(format, target, w, x, y, z);
        return pf::format;
    }
}