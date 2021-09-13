package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.AllowedTypes;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.badmapping.EmptyDateFormatException;
import ninja.javahacker.jaspasema.exceptions.badmapping.InvalidDateFormatException;
import ninja.javahacker.jaspasema.exceptions.badmapping.ReturnTypeRestrictionViolationException;
import ninja.javahacker.jaspasema.format.ReturnValueFormatter;
import ninja.javahacker.jaspasema.processor.AnnotatedMethod;
import ninja.javahacker.jaspasema.processor.ResultProcessor;
import ninja.javahacker.jaspasema.processor.ResultSerializer;
import ninja.javahacker.jaspasema.processor.ReturnedOk;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Repeatable(value = ProducesPlain.Container.class)
@ResultSerializer(processor = ProducesPlain.Processor.class)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProducesPlain {

    /**
     * If the output is a {@link LocalDate}, {@link LocalTime}, {@link LocalDateTime}, {@link Year} or {@link YearMonth},
     * specifies which is the date/time format excepted. Otherwise, should be left blank.
     * @return If the output is a {@link LocalDate}, {@link LocalTime}, {@link LocalDateTime}, {@link Year} or {@link YearMonth},
     *     specifies which is the date/time format excepted. Otherwise, should be left blank.
     */
    public String format() default "";

    /**
     * The MIME type of the output that should be produced.
     * @return The MIME type of the output that should be produced.
     */
    public String type() default "text/plain;charset=utf-8";

    public String jQueryType() default "text";

    /**
     * The HTTP status code that should be produced.
     * @return The HTTP status code that should be produced.
     */
    public int status() default 200;

    /**
     * Defines which exception triggers the behaviour described in this annotation.
     * Left it unspecified (with the default {@link ReturnedOk}) for denoting a normal return behaviour instead
     * of one triggered by the raise of an exception.
     * @return The exception which triggers the behaviour described in this annotation
     *     or unspecified (with the default {@link ReturnedOk}) for a normal return.
     */
    @ResultSerializer.ExitDiscriminator
    public Class<? extends Throwable> on() default ReturnedOk.class;

    /**
     * The class that is responsible for processing the {@link ProducesPlain} annotation.
     * @author Victor Williams Stafusa da Silva
     */
    public static class Processor implements ResultProcessor<ProducesPlain, Object> {

        /**
         * Sole constructor.
         */
        public Processor() {
        }

        /**
         * {@inheritDoc}
         * @param <E> {@inheritDoc}
         * @param meth {@inheritDoc}
         * @return {@inheritDoc}
         * @throws BadServiceMappingException {@inheritDoc}
         */
        @NonNull
        @Override
        public <E> Stub<E> prepare(@NonNull AnnotatedMethod<ProducesPlain, E> meth) throws BadServiceMappingException {
            var method = meth.getMethod();
            var annotation = meth.getAnnotation();
            var format = annotation.format();
            var target = meth.getTarget();
            var annotationClass = annotation.annotationType();
            if (annotation.on() == ReturnedOk.class) ResultProcessor.rejectForVoid(method, ProducesPlain.class);
            var parser = ReturnValueFormatter.prepare(
                    meth.getTarget(),
                    annotation.format(),
                    () -> new ReturnTypeRestrictionViolationException(method, annotationClass, AllowedTypes.DATE_TIME, target),
                    () -> new ReturnTypeRestrictionViolationException(method, annotationClass, AllowedTypes.SIMPLE, target),
                    () -> new EmptyDateFormatException(method, annotationClass),
                    () -> new InvalidDateFormatException(method, annotationClass, format)
            );
            ResultProcessor.Worker<E> w = (m, ctx, v) -> {
                ctx.result(parser.make(v));
                ctx.contentType(annotation.type());
                ctx.status(annotation.status());
            };
            return new Stub<>(w, annotation.jQueryType());
        }
    }

    /**
     * Container annotation for repeated &#64;{@link ProducesPlain}.
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Container {

        /**
         * The multiple {@link ProducesPlain} contained.
         * @return The multiple {@link ProducesPlain} contained.
         */
        public ProducesPlain[] value();
    }
}