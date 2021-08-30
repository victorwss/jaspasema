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
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.ext.ObjectUtils;
import ninja.javahacker.jaspasema.format.ParameterParser;
import ninja.javahacker.jaspasema.processor.AnnotatedParameter;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import ninja.javahacker.jaspasema.processor.ParamSource;

/**
 * Denotes that the value of a method parameter should be read from a cookie.
 *
 * <p>For example:</p>
 * <pre>
 *     &#64;Get
 *     &#64;Path("/foo")
 *     public String foo(
 *         &#64;CookieParam String bar,                                     // Uses the content of the "bar" cookie.
 *         &#64;CookieParam(name = "login-hash") String loginHash,          // Uses the content of the "login-hash" cookie.
 *         &#64;CookieParam(dateFormat = "dd/MM/uuuu") LocalDate loginDate) // Sets a date format for the cookie.
 *     {
 *         // Do stuff.
 *     }
 * </pre>
 * @author Victor Williams Stafusa da Silva
 */
@ParamSource(processor = CookieParam.Processor.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CookieParam {

    /**
     * This tells which is the desired date format for converting the value of the cookie to an instance of {@link LocalDate}
     * {@link LocalDateTime}, {@link LocalTime}, {@link Year} or {@link YearMonth}.
     * <p>Example of valid formats includes {@code "dd-MM-uuuu"}, {@code "dd/MM/uuuu HH:mm:ss"}, {@code "MM/uuuu"} among others.</p>
     * <p>This field is mandatory if the parameter type is one of the aforemetioned formats. Otherwise, this field should not be used.</p>
     * @see <a href="https://docs.oracle.com/en/java/javase/16/docs/api/java.base/java/time/format/DateTimeFormatter.html#patterns">Patterns</a>
     * @return The desired date format.
     */
    public String dateFormat() default "";

    /**
     * The name of the cookie. Uses the name of the parameter if blank or left unspecified.
     * @return The name of the cookie.
     */
    public String name() default "";

    /**
     * The class that is responsible for processing the {@link CookieParam} annotation.
     * @author Victor Williams Stafusa da Silva
     */
    public static final class Processor implements ParamProcessor<CookieParam> {

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
        public <E> Stub<E> prepare(@NonNull AnnotatedParameter<CookieParam, E> param) throws BadServiceMappingException {
            var annotation = param.getAnnotation();
            var paramName = ObjectUtils.choose(annotation.name(), param.getParameterName());
            var part = ParameterParser.prepare(param, annotation.dateFormat());
            return new Stub<>(ctx -> part.make(ctx.cookie(paramName)), "", "");
        }
    }
}