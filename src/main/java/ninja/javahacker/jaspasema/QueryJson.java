package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.ext.ObjectUtils;
import ninja.javahacker.jaspasema.processor.JsonTypesProcessor;
import ninja.javahacker.jaspasema.exceptions.ParameterValueException;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import ninja.javahacker.jaspasema.processor.ParamSource;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
@ParamSource(processor = QueryJson.Processor.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryJson {
    public boolean lenient() default false;
    public String name() default "";
    public String jsVar() default "";

    public static class Processor implements ParamProcessor<QueryJson> {

        @Override
        public <E> Stub<E> prepare(
                @NonNull ReifiedGeneric<E> target,
                @NonNull QueryJson annotation,
                @NonNull Parameter p)
        {
            String paramName = ObjectUtils.choose(annotation.name(), p.getName());
            String js = ObjectUtils.choose(annotation.jsVar(), p.getName());

            return new Stub<>(
                    (rq, rp) -> {
                        String s = rq.queryParams(paramName);
                        return JsonTypesProcessor.readJson(
                            annotation.lenient(),
                            target,
                            s,
                            x -> ParameterValueException.MalformedParameterException.create(p, QueryJson.class, s, x));
                    },
                    js,
                    "targetUrl += '&" + paramName + "=' + encodeURI(JSON.stringify(" + js + "));");
        }
    }
}