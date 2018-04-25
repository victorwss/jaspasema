package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import java.util.stream.Stream;
import lombok.NonNull;
import ninja.javahacker.jaspasema.ext.ObjectUtils;
import ninja.javahacker.jaspasema.format.ParameterParser;
import ninja.javahacker.jaspasema.processor.BadServiceMappingException;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import ninja.javahacker.jaspasema.processor.ParamSource;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
@ParamSource(processor = UriPart.Processor.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface UriPart {
    public String format() default "";
    public String name() default "";
    public String jsVar() default "";

    public static class Processor implements ParamProcessor<UriPart> {

        @Override
        public <E> Stub<E> prepare(
                @NonNull ReifiedGeneric<E> target,
                @NonNull UriPart annotation,
                @NonNull Parameter p)
                throws BadServiceMappingException
        {
            String paramName = ObjectUtils.choose(annotation.name(), p.getName());
            Path path = p.getDeclaringExecutable().getAnnotation(Path.class);
            if (path == null || !containsPart(path.value(), paramName)) {
                throw new BadServiceMappingException(p, "Parameter value do not matches anything in method's @Path value.");
            }
            String js = ObjectUtils.choose(annotation.jsVar(), p.getName());

            ParameterParser<E> part = ParameterParser.prepare(target, annotation.annotationType(), annotation.format(), p);
            return new Stub<>(
                    (rq, rp) -> part.make(rq.params(paramName)),
                    js,
                    "targetUrl = targetUrl.replace(':" + paramName + "', encodeURI(" + js + "));");
        }

        private boolean containsPart(String parts, String part) {
            String z = ":" + part;
            return Stream.of(parts.split("/")).anyMatch(z::equals);
        }
    }
}