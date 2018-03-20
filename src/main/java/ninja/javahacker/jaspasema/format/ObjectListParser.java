package ninja.javahacker.jaspasema.format;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.List;
import lombok.NonNull;
import ninja.javahacker.jaspasema.processor.BadServiceMappingException;
import ninja.javahacker.jaspasema.processor.MalformedParameterException;
import ninja.javahacker.jaspasema.processor.TargetType;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface ObjectListParser<E> {
    public List<E> make(List<String> in) throws MalformedParameterException;

    public static <E> ObjectListParser<E> prepare(
            @NonNull TargetType<List<E>> target,
            @NonNull Class<? extends Annotation> annotationClass,
            @NonNull String format,
            @NonNull Parameter p)
            throws BadServiceMappingException
    {
        String annotationName = annotationClass.getSimpleName();
        ParseFunctionList<E> pf = ParseFunctionList.parserFor(p, target);
        DateTimeParseFunctionList<E> df = DateTimeParseFunctionList.parserFor(p, target);
        if (pf == null && df == null) {
            throw new BadServiceMappingException(
                    p,
                    "The @" + annotationName + " annotation must be used only on parameters of "
                            + "Lists of primitive wrappers, Strings or date/time types.");
        }
        if (pf != null) {
            if (!format.isEmpty()) {
                throw new BadServiceMappingException(
                        p,
                        "The @" + annotationName + " format must be specified only on Lists of date/time parameters.");
            }
            return x ->
                    pf.parse(a ->
                            new MalformedParameterException(p, "Invalid value for @" + annotationName + ": \"" + x + "\".", a), x);
        }
        DateTimeFormatter dtf;
        try {
            dtf = DateTimeFormatter.ofPattern(format).withResolverStyle(ResolverStyle.STRICT);
        } catch (IllegalArgumentException e) {
            throw new BadServiceMappingException(p, "Invalid format at @" + annotationName + " annotation.");
        }
        return list ->
                df.parse(a ->
                        new MalformedParameterException(
                                p,
                                "Invalid values for @" + annotationName + ": " + list + ".",
                                a),
                        list,
                        dtf);
    }
}