package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.stream.Stream;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.badmapping.UnmatcheableParameterException;
import ninja.javahacker.jaspasema.ext.ObjectUtils;
import ninja.javahacker.jaspasema.format.ParameterParser;
import ninja.javahacker.jaspasema.processor.AnnotatedParameter;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import ninja.javahacker.jaspasema.processor.ParamSource;

/**
 * Denotes that the value of a method parameter should be read from part of the path.
 *
 * <p>For example:</p>
 * <pre>
 *     &#64;Get
 *     &#64;Path("/foo/:bar/:loginDate/:z-z")
 *     public String foo(
 *         &#64;UriPart String bar,                                     // Uses the content of the "bar" part of the URI.
 *         &#64;UriPart(dateFormat = "dd-MM-uuuu") LocalDate loginDate) // Sets a date format for the "loginDate" part of the URI.
 *         &#64;UriPart(name = "z-z") int z)                            // Sets the "z-z" part of the URI.
 *     {
 *         // Do stuff.
 *     }
 * </pre>
 * @author Victor Williams Stafusa da Silva
 */
@ParamSource(processor = UriPart.Processor.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface UriPart {

    /**
     * This tells which is the desired date format for converting the value of the URI part to an instance of {@link LocalDate}
     * {@link LocalDateTime}, {@link LocalTime}, {@link Year} or {@link YearMonth}.
     * <p>Example of valid formats includes {@code "dd-MM-uuuu"}, {@code "dd/MM/uuuu HH:mm:ss"}, {@code "MM/uuuu"} among others.</p>
     * <p>This field is mandatory if the parameter type is one of the aforemetioned formats. Otherwise, this field should not be used.</p>
     * @see <a href="https://docs.oracle.com/en/java/javase/16/docs/api/java.base/java/time/format/DateTimeFormatter.html#patterns">Patterns</a>
     * @return The desired date format.
     */
    public String dateFormat() default "";

    /**
     * The name of the URI part. Uses the name of the parameter if blank or left unspecified.
     * @return The name of the URI part.
     */
    public String name() default "";

    /**
     * Defines the name of the variable used to hold the value of this parameter in the autogenerated javascript stub.
     * If unspecified, this defaults to the parameter name.
     * @return The name of the variable used to hold the value of this parameter in the autogenerated javascript stub.
     */
    public String jsVar() default "";

    /**
     * The class that is responsible for processing the {@link UriPart} annotation.
     * @author Victor Williams Stafusa da Silva
     */
    public static class Processor implements ParamProcessor<UriPart> {

        private static final String JS_TEMPLATE = "__targetUrl = __targetUrl.replace(':$PARAM$', encodeURI($JS$));";

        /**
         * Sole constructor.
         */
        public Processor() {
        }

        /**
         * {@inheritDoc}
         * @param <E> {@inheritDoc}
         * @param param {@inheritDoc}
         * @return {@inheritDoc}
         * @throws BadServiceMappingException {@inheritDoc}
         */
        @NonNull
        @Override
        public <E> Stub<E> prepare(@NonNull AnnotatedParameter<UriPart, E> param) throws BadServiceMappingException {
            var annotation = param.getAnnotation();
            var p = param.getParameter();
            var paramName = ObjectUtils.choose(annotation.name(), param.getParameterName());
            var path = p.getDeclaringExecutable().getAnnotation(Path.class);
            if (path == null || !containsPart(path.value(), paramName)) throw new UnmatcheableParameterException(p);
            var js = ObjectUtils.choose(annotation.jsVar(), param.getParameterName());
            var t1 = JS_TEMPLATE.replace("$PARAM$", paramName).replace("$JS$", js);
            var part = ParameterParser.prepare(param, annotation.dateFormat());
            return new Stub<>(ctx -> part.make(ctx.pathParam(paramName)), js, t1);
        }

        @NonNull
        private static boolean containsPart(@NonNull String parts, @NonNull String part) {
            var z = ":" + part;
            return Stream.of(parts.split("/")).anyMatch(z::equals);
        }
    }
}