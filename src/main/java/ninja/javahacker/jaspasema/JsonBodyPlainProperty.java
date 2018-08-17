package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.function.Function;
import lombok.NonNull;
import ninja.javahacker.jaspasema.ext.ObjectUtils;
import ninja.javahacker.jaspasema.format.ParameterParser;
import ninja.javahacker.jaspasema.exceptions.BadServiceMappingException;
import ninja.javahacker.jaspasema.processor.JsonTypesProcessor;
import ninja.javahacker.jaspasema.exceptions.ParameterValueException;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import ninja.javahacker.jaspasema.processor.ParamSource;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
@ParamSource(processor = JsonBodyPlainProperty.Processor.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonBodyPlainProperty {
    public boolean required() default false;
    public String format() default "";
    public String jsVar() default "";

    public static class Processor implements ParamProcessor<JsonBodyPlainProperty> {

        private static final String INSTRUCTION_TEMPLATE = "data.#VAR# = #VAR#;";

        private static final String REQUEST_TYPE = "requestType = 'application/json; charset=utf-8';";

        private static final String JSONIFY = "data = JSON.stringify(data);";

        @Override
        public <E> Stub<E> prepare(
                @NonNull ReifiedGeneric<E> target,
                @NonNull JsonBodyPlainProperty annotation,
                @NonNull Parameter p)
                throws BadServiceMappingException
        {
            Function<Throwable, ParameterValueException.MalformedJsonBodyException> thrower =
                    e -> ParameterValueException.MalformedJsonBodyException.create(p, e);

            String js = ObjectUtils.choose(annotation.jsVar(), p.getName());

            ParameterParser<E> part = ParameterParser.prepare(target, annotation.annotationType(), annotation.format(), p);
            return new Stub<>(
                    (rq, rp) -> {
                        Map<String, Object> map = JsonTypesProcessor.readJsonMap(p, rq.body(), thrower);
                        Object obj = map.get(js);
                        if (obj == null) {
                            if (annotation.required()) {
                                throw ParameterValueException.AbsentRequiredParameterException.create(p);
                            }
                            return null;
                        }
                        return part.make(obj.toString());
                    },
                    js,
                    INSTRUCTION_TEMPLATE.replace("#VAR#", js),
                    REQUEST_TYPE,
                    JSONIFY);
        }
    }
}