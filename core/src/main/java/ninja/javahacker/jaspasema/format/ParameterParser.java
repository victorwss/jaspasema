package ninja.javahacker.jaspasema.format;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.function.Function;
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
    @SuppressFBWarnings({"LEST_LOST_EXCEPTION_STACK_TRACE", "BED_HIERARCHICAL_EXCEPTION_DECLARATION"})
    public static <E> ParameterParser<E> prepare(@NonNull AnnotatedParameter<?, E> param, @NonNull String dateFormat)
            throws ParameterTypeRestrictionViolationException, EmptyDateFormatException, InvalidDateFormatException
    {
        var p = param.getParameter();
        var annotationClass = param.getAnnotationType();
        var target = param.getTarget();

        @SuppressWarnings("unchecked")
        var x = (ParseFunction<E>) FormatterMap.PARSE_MAP.get(target);
        if (x != null) {
            if (!dateFormat.isEmpty()) throw ParameterTypeRestrictionViolationException.getFor(param, AllowedTypes.DATE_TIME).get();
            return body -> {
                var m = MalformedParameterValueException.expectingCause(param, body);
                return x.parse(m, body);
            };
        }

        @SuppressWarnings("unchecked")
        var d = (Function<DateTimeFormatter, ParseFunction<E>>) FormatterMap.PARSE_DT_MAP.get(target);
        if (d == null) throw ParameterTypeRestrictionViolationException.getFor(param, AllowedTypes.SIMPLE).get();

        if (dateFormat.isEmpty()) throw new EmptyDateFormatException(p, annotationClass);
        DateTimeFormatter dtf;
        try {
            dtf = DateTimeFormatter.ofPattern(dateFormat).withResolverStyle(ResolverStyle.STRICT);
        } catch (IllegalArgumentException e) {
            throw new InvalidDateFormatException(p, annotationClass, dateFormat);
        }

        var pf = d.apply(dtf);

        return body -> {
            var m = MalformedParameterValueException.expectingCause(param, body);
            return pf.parse(m, body);
        };
    }
}