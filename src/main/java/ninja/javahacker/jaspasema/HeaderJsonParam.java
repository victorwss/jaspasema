package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.badmapping.ImplicitWithJsVarException;
import ninja.javahacker.jaspasema.exceptions.paramvalue.MalformedParameterValueException;
import ninja.javahacker.jaspasema.ext.ObjectUtils;
import ninja.javahacker.jaspasema.processor.JsonTypesProcessor;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import ninja.javahacker.jaspasema.processor.ParamSource;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
@ParamSource(processor = HeaderJsonParam.Processor.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface HeaderJsonParam {
    public boolean lenient() default false;
    public String name() default "";
    public String jsVar() default "";
    public boolean implicit() default false;

    public static class Processor implements ParamProcessor<HeaderJsonParam> {

        @Override
        public <E> Stub<E> prepare(
                @NonNull ReifiedGeneric<E> target,
                @NonNull HeaderJsonParam annotation,
                @NonNull Parameter p)
                throws BadServiceMappingException
        {
            if (annotation.implicit() && !annotation.jsVar().isEmpty()) {
                throw ImplicitWithJsVarException.create(p, HeaderJsonParam.class);
            }
            String paramName = ObjectUtils.choose(annotation.name(), p.getName());
            String js = ObjectUtils.choose(annotation.jsVar(), p.getName());

            return new Stub<>(
                    (rq, rp) -> {
                        String s = rq.headers(paramName);
                        return JsonTypesProcessor.readJson(
                            annotation.lenient(),
                            target,
                            s,
                            x -> MalformedParameterValueException.create(p, HeaderJsonParam.class, s, x));
                    },
                    annotation.implicit() ? "" : js,
                    annotation.implicit() ? "" : "customHeaders.push({name: '" + paramName + "', value: " + js + ");");
        }
    }
}