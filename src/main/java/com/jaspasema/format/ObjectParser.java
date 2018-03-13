package br.gov.sp.prefeitura.smit.cgtic.jaspasema.format;

import br.gov.sp.prefeitura.smit.cgtic.jaspasema.processor.BadServiceMappingException;
import br.gov.sp.prefeitura.smit.cgtic.jaspasema.processor.MalformedParameterException;
import br.gov.sp.prefeitura.smit.cgtic.jaspasema.processor.TargetType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface ObjectParser<E> {
    public E make(String in) throws MalformedParameterException;

    public static <E> ObjectParser<E> prepare(
            @NonNull TargetType<E> target,
            @NonNull Class<? extends Annotation> annotationClass,
            @NonNull String format,
            @NonNull Parameter p)
            throws BadServiceMappingException
    {
        String annotationName = annotationClass.getSimpleName();
        ParseFunction<E> pf = ParseFunction.parserFor(target);
        DateTimeParseFunction<E> df = DateTimeParseFunction.parserFor(target);
        if (pf == null && df == null) {
            throw new BadServiceMappingException(
                    p,
                    "The @" + annotationName + " annotation must be used only on parameters of "
                            + "primitives, primitive wrappers, String and date/time types.");
        }
        if (pf != null) {
            if (!format.isEmpty()) {
                throw new BadServiceMappingException(
                        p,
                        "The @" + annotationName + " format must be specified only on date/time parameters.");
            }
            return body ->
                    pf.parse(e ->
                            new MalformedParameterException(
                                    p, "The value for @" + annotationName + " could not be parsed: \"" + body + "\"",
                                    e),
                            body);
        }
        DateTimeFormatter dtf;
        try {
            dtf = DateTimeFormatter.ofPattern(format).withResolverStyle(ResolverStyle.STRICT);
        } catch (IllegalArgumentException e) {
            throw new BadServiceMappingException(p, "Invalid format at @" + annotationName + " annotation.");
        }

        return body ->
                df.parse(e ->
                        new MalformedParameterException(
                                p,
                                "The @" + annotationName + " could not be parsed: \"" + body + "\". Format: \"" + dtf + "\"",
                                e),
                        body,
                        dtf);
    }
}