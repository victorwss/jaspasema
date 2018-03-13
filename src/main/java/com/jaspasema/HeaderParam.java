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
                @NonNull TargetType<E> target,
                @NonNull HeaderParam annotation,
                @NonNull Parameter p)
                throws BadServiceMappingException
        {
            if (annotation.implicit() && !annotation.jsVar().isEmpty()) {
                throw new BadServiceMappingException(p, "The @HeaderParam shouldn't have jsVar not empty and be implicit.");
            }
            String paramName = ObjectUtils.choose(annotation.name(), p.getName());
            String js = ObjectUtils.choose(annotation.jsVar(), p.getName());

            ObjectParser<E> part = ObjectParser.prepare(target, annotation.annotationType(), annotation.format(), p);

            return new Stub<>(
                    (rq, rp) -> part.make(rq.headers(paramName)),
                    annotation.implicit() ? "" : js,
                    annotation.implicit() ? "" : "customHeaders.push({name: '" + paramName + "', value: " + js + ");");
        }
    }
}