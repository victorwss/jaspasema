package br.gov.sp.prefeitura.smit.cgtic.jaspasema;

import br.gov.sp.prefeitura.smit.cgtic.jaspasema.ext.ObjectUtils;
import br.gov.sp.prefeitura.smit.cgtic.jaspasema.format.ObjectParser;
import br.gov.sp.prefeitura.smit.cgtic.jaspasema.processor.BadServiceMappingException;
import br.gov.sp.prefeitura.smit.cgtic.jaspasema.processor.ParamProcessor;
import br.gov.sp.prefeitura.smit.cgtic.jaspasema.processor.ParamSource;
import br.gov.sp.prefeitura.smit.cgtic.jaspasema.processor.TargetType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import lombok.NonNull;

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
                @NonNull TargetType<E> target,
                @NonNull CookieParam annotation,
                @NonNull Parameter p)
                throws BadServiceMappingException
        {
            String paramName = ObjectUtils.choose(annotation.name(), p.getName());

            ObjectParser<E> part = ObjectParser.prepare(target, annotation.annotationType(), annotation.format(), p);
            return new Stub<>(
                    (rq, rp) -> part.make(rq.cookie(paramName)),
                    "",
                    "");
        }
    }
}