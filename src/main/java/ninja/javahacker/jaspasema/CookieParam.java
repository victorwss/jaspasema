package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.ext.ObjectUtils;
import ninja.javahacker.jaspasema.format.ParameterParser;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import ninja.javahacker.jaspasema.processor.ParamSource;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
@ParamSource(processor = CookieParam.CookieProcessor.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CookieParam {
    public String format() default "";
    public String name() default "";

    public static class CookieProcessor implements ParamProcessor<CookieParam> {

        @Override
        public <E> Stub<E> prepare(
                @NonNull ReifiedGeneric<E> target,
                @NonNull CookieParam annotation,
                @NonNull Parameter p)
                throws BadServiceMappingException
        {
            String paramName = ObjectUtils.choose(annotation.name(), p.getName());

            ParameterParser<E> part = ParameterParser.prepare(target, annotation.annotationType(), annotation.format(), p);
            return new Stub<>(
                    (rq, rp) -> part.make(rq.cookie(paramName)),
                    "",
                    "");
        }
    }
}