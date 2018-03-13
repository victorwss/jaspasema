package br.gov.sp.prefeitura.smit.cgtic.jaspasema;

import br.gov.sp.prefeitura.smit.cgtic.jaspasema.ext.ObjectUtils;
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
                @NonNull TargetType<E> target,
                @NonNull QueryJson annotation,
                @NonNull Parameter p)
        {
            String paramName = ObjectUtils.choose(annotation.name(), p.getName());
            String js = ObjectUtils.choose(annotation.jsVar(), p.getName());

            return new Stub<>(
                    (rq, rp) -> JsonTypesProcessor.readJson(
                            annotation.lenient(),
                            x -> new MalformedParameterException(p, "The @QueryJson parameter has not a valid value.", x),
                            target,
                            rq.queryParams(paramName)),
                    js,
                    "targetUrl += '&" + paramName + "=' + encodeURI(JSON.stringify(" + js + "));");
        }
    }
}