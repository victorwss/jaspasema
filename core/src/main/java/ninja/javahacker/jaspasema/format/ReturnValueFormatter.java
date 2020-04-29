package ninja.javahacker.jaspasema.format;

import edu.umd.cs.findbugs.annotations.Nullable;
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
    @Nullable
    public String make(@Nullable E in);

    public static <E> ReturnValueFormatter<E> prepare(
            @NonNull ReifiedGeneric<E> target,
            @NonNull Class<? extends Annotation> annotationClass,
            @NonNull String format,
            @NonNull Method method)
            throws BadServiceMappingException
    {
        Supplier<TypeRestrictionViolationException> w = () -> new TypeRestrictionViolationException(
                    method,
                    annotationClass,
                    TypeRestrictionViolationException.AllowedTypes.SIMPLE,
                    target);

        Supplier<TypeRestrictionViolationException> x = () -> new TypeRestrictionViolationException(
                    method,
                    annotationClass,
                    TypeRestrictionViolationException.AllowedTypes.DATE_TIME,
                    target);

        Supplier<EmptyDateFormatException> y = () -> new EmptyDateFormatException(method, annotationClass);
        Supplier<InvalidDateFormatException> z = () -> new InvalidDateFormatException(method, annotationClass, format);

        var pf = FormatterFunction.formatterFor(format, target, w, x, y, z);
        return pf::format;
    }
}