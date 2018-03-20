package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import java.util.Map;
import lombok.NonNull;
import ninja.javahacker.jaspasema.ext.ObjectUtils;
import ninja.javahacker.jaspasema.format.ObjectParser;
import ninja.javahacker.jaspasema.processor.BadServiceMappingException;
import ninja.javahacker.jaspasema.processor.JsonTypesProcessor;
import ninja.javahacker.jaspasema.processor.MalformedParameterException;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import ninja.javahacker.jaspasema.processor.ParamSource;
import ninja.javahacker.jaspasema.processor.TargetType;

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