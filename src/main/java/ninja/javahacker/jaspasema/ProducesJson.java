package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import lombok.NonNull;
import ninja.javahacker.jaspasema.processor.BadServiceMappingException;
import ninja.javahacker.jaspasema.processor.JsonTypesProcessor;
import ninja.javahacker.jaspasema.processor.ReturnProcessor;
import ninja.javahacker.jaspasema.processor.ReturnSerializer;
import ninja.javahacker.jaspasema.processor.TargetType;

/**
 * @author Victor Williams Stafusa da Silva
 */
@ReturnSerializer(processor = ProducesJson.Processor.class)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProducesJson {
    public boolean lenient() default false;

    public static class Processor implements ReturnProcessor<ProducesJson> {
        @Override
        public <E> Stub<E> prepare(
                @NonNull TargetType<E> target,
                @NonNull ProducesJson annotation,
                @NonNull Method method)
                throws BadServiceMappingException
        {
            return new Stub<>(v -> JsonTypesProcessor.writeJson(RuntimeException::new, v), "json");
        }
    }
}