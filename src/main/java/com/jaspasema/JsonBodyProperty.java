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
import java.util.Map;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@ParamSource(processor = JsonBodyProperty.Processor.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonBodyProperty {
    public boolean required() default false;
    public boolean lenient() default false;
    public String jsVar() default "";

    public static class Processor implements ParamProcessor<JsonBodyProperty> {

        @Override
        public <E> Stub<E> prepare(
                @NonNull TargetType<E> target,
                @NonNull JsonBodyProperty annotation,
                @NonNull Parameter p)
        {
            String js = ObjectUtils.choose(annotation.jsVar(), p.getName());

            return new Stub<>(
                    (rq, rp) -> {
                        Map<String, Object> map = JsonTypesProcessor.readJsonMap(p, rq.body());
                        Object obj = map.get(js);
                        if (obj == null) {
                            if (annotation.required()) throw new MalformedParameterException(p, "Required parameter was absent.");
                            return null;
                        }
                        return JsonTypesProcessor.convert(annotation.lenient(), obj, target);
                    },
                    js,
                    "data." + js + " = " + js + ";");
        }
    }
}