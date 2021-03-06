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
    public boolean lenient() default false;
    public String type() default "text/json;charset=utf-8";
    public int status() default 200;

    @ResultSerializer.ExitDiscriminator
    public Class<? extends Throwable> on() default ReturnedOk.class;

    public static class Processor implements ResultProcessor<ProducesJson, Object> {

        @NonNull
        @Override
        public <E> Stub<E> prepare(@NonNull AnnotatedMethod<ProducesJson, E> meth) throws BadServiceMappingException {
            var method = meth.getMethod();
            var annotation = meth.getAnnotation();
            if (annotation.on() == ReturnedOk.class) ResultProcessor.rejectForVoid(method, ProducesJson.class);
            ResultProcessor.Worker<E> w = (m, ctx, v) -> {
                ctx.result(toJson(annotation.lenient(), method, v));
                ctx.contentType(annotation.type());
                ctx.status(annotation.status());
            };
            return new Stub<>(w, "json");
        }

        @NonNull
        private static <E> String toJson(
                boolean lenient,
                @NonNull Method method,
                @NonNull E someObject)
                throws MalformedJsonReturnValueException
        {
            try {
                var x = JsonTypesProcessor.writeJson(lenient, someObject);
                return x == null ? "" : x;
            } catch (JsonProcessingException x) {
                throw new MalformedJsonReturnValueException(method, someObject, x);
            }
        }
    }

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Container {
        public ProducesJson[] value();
    }
}