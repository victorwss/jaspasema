package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.function.Function;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.paramvalue.AbsentRequiredParameterException;
import ninja.javahacker.jaspasema.exceptions.paramvalue.MalformedJsonBodyException;
import ninja.javahacker.jaspasema.ext.ObjectUtils;
import ninja.javahacker.jaspasema.processor.JsonTypesProcessor;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import ninja.javahacker.jaspasema.processor.ParamSource;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
@ParamSource(processor = JsonBodyProperty.Processor.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonBodyProperty {
    public boolean required() default false;
    public boolean lenient() default false;
    public String jsVar() default "";

    public static class Processor implements ParamProcessor<JsonBodyProperty> {

        private static final String INSTRUCTION_TEMPLATE = "data.#VAR# = #VAR#;";

        private static final String REQUEST_TYPE = "requestType = 'application/json; charset=utf-8';";

        private static final String JSONIFY = "data = JSON.stringify(data);";

        @Override
        public <E> Stub<E> prepare(
                @NonNull ReifiedGeneric<E> target,
                @NonNull JsonBodyProperty annotation,
                @NonNull Parameter p)
        {
            Function<Throwable, MalformedJsonBodyException> thrower =
                    e -> new MalformedJsonBodyException(p, e);

            String js = ObjectUtils.choose(annotation.jsVar(), p.getName());

            return new Stub<>(
                    ctx -> {
                        Map<String, Object> map = JsonTypesProcessor.readJsonMap(ctx.body(), thrower);
                        Object obj = map.get(js);
                        if (obj != null) return JsonTypesProcessor.convert(annotation.lenient(), obj, target);
                        if (annotation.required()) throw new AbsentRequiredParameterException(p);
                        return null;
                    },
                    js,
                    INSTRUCTION_TEMPLATE.replace("#VAR#", js),
                    REQUEST_TYPE,
                    JSONIFY);
        }
    }
}