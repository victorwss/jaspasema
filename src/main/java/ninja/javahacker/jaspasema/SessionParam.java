package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.ext.ObjectUtils;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import ninja.javahacker.jaspasema.processor.ParamSource;
import ninja.javahacker.jaspasema.processor.TargetType;

/**
 * @author Victor Williams Stafusa da Silva
 */
@ParamSource(processor = SessionParam.Processor.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface SessionParam {
    public String format() default "";
    public String name() default "";

    public static class Processor implements ParamProcessor<SessionParam> {

        @Override
        public <E> Stub<E> prepare(
                @NonNull TargetType<E> target,
                @NonNull SessionParam annotation,
                @NonNull Parameter p)
        {
            String paramName = ObjectUtils.choose(annotation.name(), p.getName());
            return new Stub<>((rq, rp) -> rq.session().attribute(paramName), "", "");
        }
    }
}