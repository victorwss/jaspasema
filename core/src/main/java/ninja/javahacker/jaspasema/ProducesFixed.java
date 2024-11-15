package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.processor.AnnotatedMethod;
import ninja.javahacker.jaspasema.processor.ResultProcessor;
import ninja.javahacker.jaspasema.processor.ResultSerializer;
import ninja.javahacker.jaspasema.processor.ReturnedOk;

/**
 * Denotes a fixed output given by the annotated method {@link #value()}.
 * @author Victor Williams Stafusa da Silva
 */
@Repeatable(value = ProducesFixed.Container.class)
@ResultSerializer(processor = ProducesFixed.Processor.class)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProducesFixed {

    /**
     * What should be the produced output.
     * @return What should be the produced output.
     */
    public String value() default "";

    /**
     * The MIME type of the output that should be produced.
     * @return The MIME type of the output that should be produced.
     */
    public String type() default "text/html;charset=utf-8";

    /**
     * The result type that jQuery should expected.
     * @return The result type that jQuery should expected.
     */
    public String jQueryType() default "html";

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
     * The class that is responsible for processing the {@link ProducesFixed} annotation.
     * @author Victor Williams Stafusa da Silva
     */
    public static final class Processor implements ResultProcessor<ProducesFixed, Object> {

        /**
         * Sole constructor.
         */
        public Processor() {
        }

        /**
         * {@inheritDoc}
         * @param <E> {@inheritDoc}
         * @param annotated {@inheritDoc}
         * @return {@inheritDoc}
         * @throws BadServiceMappingException {@inheritDoc}
         */
        @NonNull
        @Override
        public <E> Stub<E> prepare(@NonNull AnnotatedMethod<ProducesFixed, E> annotated) throws BadServiceMappingException {
            var annotation = annotated.getAnnotation();
            ResultProcessor.Worker<E> w = (ctx, v) -> {
                ctx.result(annotation.value());
                ctx.contentType(annotation.type());
                ctx.status(annotation.status());
            };
            return new Stub<>(w, annotation.jQueryType());
        }
    }

    /**
     * Container annotation for repeated &#64;{@link ProducesFixed}.
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Container {

        /**
         * The multiple {@link ProducesFixed} contained.
         * @return The multiple {@link ProducesFixed} contained.
         */
        public ProducesFixed[] value();
    }
}