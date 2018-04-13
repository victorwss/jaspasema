package ninja.javahacker.jaspasema;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
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
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Repeatable(value = ProducesJson.Container.class)
@ReturnSerializer(processor = ProducesJson.Processor.class)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProducesJson {
    public boolean lenient() default false;
    public String type() default "text/json;charset=utf-8";
    public int status() default 200;

    @ReturnSerializer.ExitDiscriminator
    public Class<? extends Throwable> on() default ReturnedOk.class;

    public static class Processor implements ReturnProcessor<ProducesJson> {
        @Override
        public <E> Stub<E> prepare(
                @NonNull ReifiedGeneric<E> target,
                @NonNull ProducesJson annotation,
                @NonNull Method method)
                throws BadServiceMappingException
        {
            if (annotation.on() == ReturnedOk.class) ReturnProcessor.rejectForVoid(method, ProducesJson.class);
            return new Stub<>((rq, rp, v) -> {
                rp.body(toJson(annotation.lenient(), method, v));
                rp.type(annotation.type());
                rp.status(annotation.status());
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

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Container {
        public ProducesJson[] value();
    }
}