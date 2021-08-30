package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
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
    public String format() default "";
    public String type() default "text/plain;charset=utf-8";
    public String jQueryType() default "text";
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

        @NonNull
        @Override
        public <E> Stub<E> prepare(@NonNull AnnotatedMethod<ProducesPlain, E> meth) throws BadServiceMappingException {
            var method = meth.getMethod();
            var annotation = meth.getAnnotation();
            if (annotation.on() == ReturnedOk.class) ResultProcessor.rejectForVoid(method, ProducesPlain.class);
            var parser = ReturnValueFormatter.prepare(meth.getTarget(), annotation.annotationType(), annotation.format(), method);
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