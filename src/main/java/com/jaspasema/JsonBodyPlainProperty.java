package br.gov.sp.prefeitura.smit.cgtic.jaspasema;

import br.gov.sp.prefeitura.smit.cgtic.jaspasema.ext.ObjectUtils;
import br.gov.sp.prefeitura.smit.cgtic.jaspasema.format.ObjectParser;
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
import java.util.Map;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@ParamSource(processor = JsonBodyPlainProperty.Processor.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonBodyPlainProperty {
    public boolean required() default false;
    public String format() default "";
    public String jsVar() default "";

    public static class Processor implements ParamProcessor<JsonBodyPlainProperty> {

        @Override
        public <E> Stub<E> prepare(
                @NonNull TargetType<E> target,
                @NonNull JsonBodyPlainProperty annotation,
                @NonNull Parameter p)
                throws BadServiceMappingException
        {
            String js = ObjectUtils.choose(annotation.jsVar(), p.getName());

            ObjectParser<E> part = ObjectParser.prepare(target, annotation.annotationType(), annotation.format(), p);
            return new Stub<>(
                    (rq, rp) -> {
                        Map<String, Object> map = JsonTypesProcessor.readJsonMap(p, rq.body());
                        Object obj = map.get(js);
                        if (obj == null) {
                            if (annotation.required()) throw new MalformedParameterException(p, "Required parameter was absent.");
                            return null;
                        }
                        return part.make(obj.toString());
                    },
                    js,
                    "data." + js + " = " + js + ";");
        }
    }
}