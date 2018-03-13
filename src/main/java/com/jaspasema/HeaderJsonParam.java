package br.gov.sp.prefeitura.smit.cgtic.jaspasema;

import br.gov.sp.prefeitura.smit.cgtic.jaspasema.ext.ObjectUtils;
import br.gov.sp.prefeitura.smit.cgtic.jaspasema.processor.BadServiceMappingException;
import br.gov.sp.prefeitura.smit.cgtic.jaspasema.processor.JsonTypesProcessor;
import br.gov.sp.prefeitura.smit.cgtic.jaspasema.processor.MalformedParameterException;
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
                @NonNull TargetType<E> target,
                @NonNull HeaderJsonParam annotation,
                @NonNull Parameter p)
                throws BadServiceMappingException
        {
            if (annotation.implicit() && !annotation.jsVar().isEmpty()) {
                throw new BadServiceMappingException(p, "The @HeaderJsonParam shouldn't have jsVar not empty and be implicit.");
            }
            String paramName = ObjectUtils.choose(annotation.name(), p.getName());
            String js = ObjectUtils.choose(annotation.jsVar(), p.getName());

            return new Stub<>(
                    (rq, rp) -> JsonTypesProcessor.readJson(
                            annotation.lenient(),
                            x -> new MalformedParameterException(p, "The @HeaderJsonJson parameter has not a valid value.", x),
                            target,
                            rq.headers(paramName)),
                    annotation.implicit() ? "" : js,
                    annotation.implicit() ? "" : "customHeaders.push({name: '" + paramName + "', value: " + js + ");");
        }
    }
}