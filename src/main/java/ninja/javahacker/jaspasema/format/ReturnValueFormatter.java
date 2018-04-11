package ninja.javahacker.jaspasema.format;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import lombok.NonNull;
import ninja.javahacker.jaspasema.processor.BadServiceMappingException;
import ninja.javahacker.jaspasema.processor.TargetType;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface ReturnValueFormatter<E> {
    public String make(E in);

    public static <E> ReturnValueFormatter<E> prepare(
            @NonNull TargetType<E> target,
            @NonNull Class<? extends Annotation> annotationClass,
            @NonNull String format,
            @NonNull Method method)
            throws BadServiceMappingException
    {
        String annotationName = annotationClass.getSimpleName();
        FormatterFunction<E> pf = FormatterFunction.formatterFor(target);
        DateTimeFormatterFunction<E> df = DateTimeFormatterFunction.formatterFor(target);
        if (pf == null && df == null) {
            throw new BadServiceMappingException(
                    method,
                    "The @" + annotationName + " annotation must be used only on methods returning "
                            + "primitives, primitive wrappers, String or date/time types. The found type was " + target + ".");
        }
        if (pf != null) {
            if (!format.isEmpty()) {
                throw new BadServiceMappingException(
                        method,
                        "The @" + annotationName + " format must be specified only on date/time-returning methods. "
                                + "The found type was " + target + ".");
            }
            return pf::format;
        }
        DateTimeFormatter dtf;
        try {
            dtf = DateTimeFormatter.ofPattern(format).withResolverStyle(ResolverStyle.STRICT);
        } catch (IllegalArgumentException e) {
            throw new BadServiceMappingException(method, "Invalid format at @" + annotationName + " annotation.");
        }
        return body -> df.format(body, dtf);
    }
}