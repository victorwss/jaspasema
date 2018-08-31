package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.format.ReturnValueFormatter;
import ninja.javahacker.jaspasema.processor.ReturnProcessor;
import ninja.javahacker.jaspasema.processor.ReturnSerializer;
import ninja.javahacker.jaspasema.processor.ReturnedOk;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Repeatable(value = ProducesPlain.Container.class)
@ReturnSerializer(processor = ProducesPlain.Processor.class)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProducesPlain {
    public String format() default "";
    public String type() default "text/plain;charset=utf-8";
    public String jQueryType() default "text";
    public int status() default 200;

    @ReturnSerializer.ExitDiscriminator
    public Class<? extends Throwable> on() default ReturnedOk.class;

    public static class Processor implements ReturnProcessor<ProducesPlain> {
        @Override
        public <E> Stub<E> prepare(
                @NonNull ReifiedGeneric<E> target,
                @NonNull ProducesPlain annotation,
                @NonNull Method method)
                throws BadServiceMappingException
        {
            if (annotation.on() == ReturnedOk.class) ReturnProcessor.rejectForVoid(method, ProducesPlain.class);
            ReturnValueFormatter<E> parser = ReturnValueFormatter.prepare(target, annotation.annotationType(), annotation.format(), method);
            return new Stub<>((m, rq, rp, v) -> {
                rp.body(parser.make(v));
                rp.type(annotation.type());
                rp.status(annotation.status());
            }, annotation.jQueryType());
        }
    }

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Container {
        public ProducesPlain[] value();
    }
}