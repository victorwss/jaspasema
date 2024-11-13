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
import ninja.javahacker.jaspasema.format.ParameterParser;
import ninja.javahacker.jaspasema.processor.AnnotatedParameter;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import ninja.javahacker.jaspasema.processor.ParamSource;

/**
 * Denotes that the value of a method parameter should be read from the request body as plain text before being desserialized.
 *
 * <p>For example:</p>
 * <pre>
 *     // Reads the contents of the body as a String.
 *     &#64;Post
 *     &#64;Path("/foo1")
 *     public String foo1(&#64;PlainBody String bar) {
 *         // Do stuff.
 *     }
 *
 *     // Reads the contents of the body as a LocalDate.
 *     &#64;Post
 *     &#64;Path("/foo2")
 *     public String foo2(&#64;PlainBody(dateFormat = "dd-MM-uuuu") LocalDate bar) {
 *         // Do stuff.
 *     }
 *
 *     // Reads the contents of the body as an int.
 *     &#64;Post
 *     &#64;Path("/foo3")
 *     public String foo3(&#64;PlainBody(dateFormat = "dd-MM-uuuu") int bar) {
 *         // Do stuff.
 *     }
 * </pre>
 * @author Victor Williams Stafusa da Silva
 */
@ParamSource(processor = PlainBody.Processor.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PlainBody {

    /**
     * This tells which is the desired date format for converting the value of the body to an instance of {@link LocalDate}
     * {@link LocalDateTime}, {@link LocalTime}, {@link Year} or {@link YearMonth}.
     * <p>Example of valid formats includes {@code "dd-MM-uuuu"}, {@code "dd/MM/uuuu HH:mm:ss"}, {@code "MM/uuuu"} among others.</p>
     * <p>This field is mandatory if the parameter type is one of the aforemetioned formats. Otherwise, this field should not be used.</p>
     * @see <a href="https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/time/format/DateTimeFormatter.html#patterns">Patterns</a>
     * @return The desired date format.
     */
    public String dateFormat() default "";

    /**
     * The class that is responsible for processing the {@link PlainBody} annotation.
     * @author Victor Williams Stafusa da Silva
     */
    public static final class Processor implements ParamProcessor<PlainBody> {

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
        public <E> Stub<E> prepare(@NonNull AnnotatedParameter<PlainBody, E> param) throws BadServiceMappingException {
            var annotation = param.getAnnotation();
            var part = ParameterParser.prepare(param, annotation.dateFormat());
            return new Stub<>(ctx -> part.make(ctx.body()), "", "");
        }
    }
}