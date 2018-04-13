package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import ninja.javahacker.jaspasema.ext.ObjectUtils;
import ninja.javahacker.jaspasema.processor.BadServiceMappingException;
import ninja.javahacker.jaspasema.processor.JsonTypesProcessor;
import ninja.javahacker.jaspasema.processor.MalformedParameterException;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import ninja.javahacker.jaspasema.processor.ParamSource;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
@ParamSource(processor = QueryJsons.Processor.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryJsons {
    public boolean lenient() default false;
    public String name() default "";
    public String jsVar() default "";

    public static class Processor implements ParamProcessor<QueryJsons> {

        @Override
        @SuppressWarnings("unchecked")
        public <E> Stub<E> prepare(
                @NonNull ReifiedGeneric<E> target,
                @NonNull QueryJsons annotation,
                @NonNull Parameter p)
                throws BadServiceMappingException
        {
            if (!target.raw().isAssignableFrom(List.class)) {
                throw new BadServiceMappingException(p, "The @QueryJsons should be used only in List types.");
            }
            String paramName = ObjectUtils.choose(annotation.name(), p.getName());
            String js = ObjectUtils.choose(annotation.jsVar(), p.getName());

            return new Stub<>(
                    prepareList((ReifiedGeneric) target, annotation, p, paramName),
                    js,
                    ""
                            + "for (var elem in " + js + ") {\n"
                            + "    targetUrl += '&" + paramName + "=' + encodeURI(JSON.stringify(" + js + "[elem]));\n"
                            + "}"
            );
        }

        private <E> ParamProcessor.Worker<List<E>> prepareList(
                @NonNull ReifiedGeneric<List<E>> target,
                @NonNull QueryJsons annotation,
                @NonNull Parameter p,
                @NonNull String paramName)
        {
            return (rq, rp) -> {
                List<E> elements = new ArrayList<>();
                for (String s : rq.queryParamsValues(paramName)) {
                    E elem = JsonTypesProcessor.readJson(
                            annotation.lenient(),
                            x -> new MalformedParameterException(p, "The @QueryJsons parameter has not a valid value.", x),
                            ReifiedGeneric.unwrapIterableGenericType(target),
                            s);
                    elements.add(elem);
                }
                return elements;
            };
        }
    }
}