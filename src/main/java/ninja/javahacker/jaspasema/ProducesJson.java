package ninja.javahacker.jaspasema;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import lombok.NonNull;
import ninja.javahacker.jaspasema.processor.BadServiceMappingException;
import ninja.javahacker.jaspasema.processor.JsonTypesProcessor;
import ninja.javahacker.jaspasema.processor.MalformedReturnValueException;
import ninja.javahacker.jaspasema.processor.ReturnProcessor;
import ninja.javahacker.jaspasema.processor.ReturnSerializer;
import ninja.javahacker.jaspasema.processor.ReturnedOk;
import ninja.javahacker.jaspasema.processor.TargetType;

/**
 * @author Victor Williams Stafusa da Silva
 */
@ReturnSerializer(processor = ProducesJson.Processor.class)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProducesJson {
    public boolean lenient() default false;
    public String type() default "text/json;charset=utf-8";
    public Class<? extends Throwable> on() default ReturnedOk.class;

    public static class Processor implements ReturnProcessor<ProducesJson> {
        @Override
        public <E> Stub<E> prepare(
                @NonNull TargetType<E> target,
                @NonNull ProducesJson annotation,
                @NonNull Method method)
                throws BadServiceMappingException
        {
            return new Stub<>((rq, rp, v) -> {
                rp.body(toJson(annotation.lenient(), method, v));
                rp.type(annotation.type());
            }, "json");
        }

        private static <E> String toJson(boolean lenient, Method method, E someObject) throws MalformedReturnValueException {
            try {
                return JsonTypesProcessor.writeJson(lenient, someObject);
            } catch (JsonProcessingException x) {
                throw new MalformedReturnValueException(
                        someObject,
                        method,
                        "Returned value couldn't be converted to JSON.",
                        x);
            }
        }
    }
}