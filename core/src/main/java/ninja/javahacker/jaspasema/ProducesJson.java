package ninja.javahacker.jaspasema;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.retvalue.MalformedJsonReturnValueException;
import ninja.javahacker.jaspasema.processor.AnnotatedMethod;
import ninja.javahacker.jaspasema.processor.JsonTypesProcessor;
import ninja.javahacker.jaspasema.processor.ResultProcessor;
import ninja.javahacker.jaspasema.processor.ResultSerializer;
import ninja.javahacker.jaspasema.processor.ReturnedOk;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Repeatable(value = ProducesJson.Container.class)
@ResultSerializer(processor = ProducesJson.Processor.class)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProducesJson {

    /**
     * The MIME type of the output that should be produced.
     * @return The MIME type of the output that should be produced.
     */
    public String type() default "text/json;charset=utf-8";

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
     * The class that is responsible for processing the {@link ProducesJson} annotation.
     * @author Victor Williams Stafusa da Silva
     */
    public static class Processor implements ResultProcessor<ProducesJson, Object> {

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
        public <E> Stub<E> prepare(@NonNull AnnotatedMethod<ProducesJson, E> meth) throws BadServiceMappingException {
            var method = meth.getMethod();
            var annotation = meth.getAnnotation();
            if (annotation.on() == ReturnedOk.class) ResultProcessor.rejectForVoid(method, ProducesJson.class);
            ResultProcessor.Worker<E> w = (m, ctx, v) -> {
                ctx.result(toJson(method, v));
                ctx.contentType(annotation.type());
                ctx.status(annotation.status());
            };
            return new Stub<>(w, "json");
        }

        @NonNull
        private static <E> String toJson(
                @NonNull Method method,
                @NonNull E someObject)
                throws MalformedJsonReturnValueException
        {
            try {
                var x = JsonTypesProcessor.writeJson(someObject);
                return x == null ? "" : x;
            } catch (JsonProcessingException x) {
                throw new MalformedJsonReturnValueException(method, someObject, x);
            }
        }
    }

    /**
     * Container annotation for repeated &#64;{@link ProducesJson}.
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Container {

        /**
         * The multiple {@link ProducesJson} contained.
         * @return The multiple {@link ProducesJson} contained.
         */
        public ProducesJson[] value();
    }
}