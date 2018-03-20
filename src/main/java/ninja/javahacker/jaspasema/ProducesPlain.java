package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import lombok.NonNull;
import ninja.javahacker.jaspasema.format.ObjectFormatter;
import ninja.javahacker.jaspasema.processor.BadServiceMappingException;
import ninja.javahacker.jaspasema.processor.ReturnProcessor;
import ninja.javahacker.jaspasema.processor.ReturnSerializer;
import ninja.javahacker.jaspasema.processor.TargetType;

/**
 * @author Victor Williams Stafusa da Silva
 */
@ReturnSerializer(processor = ProducesPlain.Processor.class)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProducesPlain {
    public String format() default "";

    public static class Processor implements ReturnProcessor<ProducesPlain> {
        @Override
        public <E> Stub<E> prepare(
                @NonNull TargetType<E> target,
                @NonNull ProducesPlain annotation,
                @NonNull Method method)
                throws BadServiceMappingException
        {
            ObjectFormatter<E> parser = ObjectFormatter.prepare(target, annotation.annotationType(), annotation.format(), method);
            return new Stub<>(v -> parser.make(v), "text");
        }
    }
}