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
     */
    public static class Processor implements ParamProcessor<PlainBody> {

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