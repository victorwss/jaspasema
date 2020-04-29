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
 * @author Victor Williams Stafusa da Silva
 */
@Repeatable(value = ProducesFixed.Container.class)
@ResultSerializer(processor = ProducesFixed.Processor.class)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProducesFixed {
    public String value() default "";
    public String type() default "text/html;charset=utf-8";
    public String jQueryType() default "html";
    public int status() default 200;

    @ResultSerializer.ExitDiscriminator
    public Class<? extends Throwable> on() default ReturnedOk.class;

    public static class Processor implements ResultProcessor<ProducesFixed, Object> {

        @NonNull
        @Override
        public <E> Stub<E> prepare(@NonNull AnnotatedMethod<ProducesFixed, E> meth) throws BadServiceMappingException {
            var annotation = meth.getAnnotation();
            ResultProcessor.Worker<E> w = (m, ctx, v) -> {
                ctx.result(annotation.value());
                ctx.contentType(annotation.type());
                ctx.status(annotation.status());
            };
            return new Stub<>(w, annotation.jQueryType());
        }
    }

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Container {
        public ProducesFixed[] value();
    }
}