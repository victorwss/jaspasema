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
            String jsVar = annotation.jsVar();
            String paramName = p.getName();
            if (annotation.implicit() && !jsVar.isEmpty()) throw new ImplicitWithJsVarException(p, HeaderJsonParam.class);
            String choosenName = ObjectUtils.choose(annotation.name(), paramName);
            String js = ObjectUtils.choose(jsVar, p.getName());

            return new Stub<>(
                    (rq, rp) -> {
                        String s = rq.headers(choosenName);
                        return JsonTypesProcessor.readJson(
                            annotation.lenient(),
                            target,
                            s,
                            x -> new MalformedParameterValueException(p, HeaderJsonParam.class, s, x));
                    },
                    annotation.implicit() ? "" : js,
                    annotation.implicit() ? "" : "customHeaders.push({name: '" + choosenName + "', value: " + js + ");");
        }
    }
}