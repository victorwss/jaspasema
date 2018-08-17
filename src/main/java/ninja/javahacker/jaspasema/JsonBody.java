package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.ext.ObjectUtils;
import ninja.javahacker.jaspasema.exceptions.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.ImplicitWithJsVarException;
import ninja.javahacker.jaspasema.processor.JsonTypesProcessor;
import ninja.javahacker.jaspasema.exceptions.ParameterValueException;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import ninja.javahacker.jaspasema.processor.ParamSource;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
@ParamSource(processor = JsonBody.Processor.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonBody {
    public boolean lenient() default false;
    public String jsVar() default "";
    public boolean implicit() default false;

    public static class Processor implements ParamProcessor<JsonBody> {

        private static final String INSTRUCTION_TEMPLATE = "data = #VAR#;";

        private static final String REQUEST_TYPE = "requestType = 'application/json; charset=utf-8';";

        private static final String JSONIFY = "data = JSON.stringify(data);";

        @Override
        public <E> Stub<E> prepare(
                @NonNull ReifiedGeneric<E> target,
                @NonNull JsonBody annotation,
                @NonNull Parameter p)
                throws BadServiceMappingException
        {
            if (annotation.implicit() && !annotation.jsVar().isEmpty()) {
                throw ImplicitWithJsVarException.create(p, JsonBody.class);
            }
            String js = ObjectUtils.choose(annotation.jsVar(), p.getName());

            return new Stub<>(
                    (rq, rp) -> {
                        String s = rq.body();
                        return JsonTypesProcessor.readJson(
                            annotation.lenient(),
                            target,
                            s,
                            x -> ParameterValueException.MalformedParameterException.create(p, JsonBody.class, s, x));
                    },
                    annotation.implicit() ? "" : js,
                    annotation.implicit() ? "" : INSTRUCTION_TEMPLATE.replace("#VAR#", js),
                    REQUEST_TYPE,
                    JSONIFY);
        }
    }
}