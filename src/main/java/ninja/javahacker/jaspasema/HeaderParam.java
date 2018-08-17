package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.ext.ObjectUtils;
import ninja.javahacker.jaspasema.format.ParameterParser;
import ninja.javahacker.jaspasema.exceptions.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.ImplicitWithJsVarException;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import ninja.javahacker.jaspasema.processor.ParamSource;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
@ParamSource(processor = HeaderParam.HeaderProcessor.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface HeaderParam {
    public String format() default "";
    public String name() default "";
    public String jsVar() default "";
    public boolean implicit() default false;

    public static class HeaderProcessor implements ParamProcessor<HeaderParam> {

        @Override
        public <E> Stub<E> prepare(
                @NonNull ReifiedGeneric<E> target,
                @NonNull HeaderParam annotation,
                @NonNull Parameter p)
                throws BadServiceMappingException
        {
            if (annotation.implicit() && !annotation.jsVar().isEmpty()) {
                throw ImplicitWithJsVarException.create(p, HeaderParam.class);
            }
            String paramName = ObjectUtils.choose(annotation.name(), p.getName());
            String js = ObjectUtils.choose(annotation.jsVar(), p.getName());

            ParameterParser<E> part = ParameterParser.prepare(target, annotation.annotationType(), annotation.format(), p);

            return new Stub<>(
                    (rq, rp) -> part.make(rq.headers(paramName)),
                    annotation.implicit() ? "" : js,
                    annotation.implicit() ? "" : "customHeaders.push({name: '" + paramName + "', value: " + js + ");");
        }
    }
}