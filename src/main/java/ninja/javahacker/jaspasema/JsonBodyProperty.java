package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import java.util.Map;
import lombok.NonNull;
import ninja.javahacker.jaspasema.ext.ObjectUtils;
import ninja.javahacker.jaspasema.processor.JsonTypesProcessor;
import ninja.javahacker.jaspasema.processor.MalformedParameterException;
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
            String js = ObjectUtils.choose(annotation.jsVar(), p.getName());

            return new Stub<>(
                    (rq, rp) -> {
                        Map<String, Object> map = JsonTypesProcessor.readJsonMap(p, rq.body());
                        Object obj = map.get(js);
                        if (obj == null) {
                            if (annotation.required()) throw new MalformedParameterException(p, "Required parameter was absent.");
                            return null;
                        }
                        return JsonTypesProcessor.convert(annotation.lenient(), obj, target);
                    },
                    js,
                    INSTRUCTION_TEMPLATE.replace("#VAR#", js),
                    REQUEST_TYPE,
                    JSONIFY);
        }
    }
}