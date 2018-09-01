package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.badmapping.TypeRestrictionViolationException;
import ninja.javahacker.jaspasema.exceptions.paramvalue.MalformedParameterValueException;
import ninja.javahacker.jaspasema.ext.ObjectUtils;
import ninja.javahacker.jaspasema.processor.JsonTypesProcessor;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import ninja.javahacker.jaspasema.processor.ParamSource;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;
import ninja.javahacker.reifiedgeneric.Wrappers;

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

        private static final String TEMPLATE = ""
                + "for (var elem in $JS$) {\n"
                + "    targetUrl += '&$PN$=' + encodeURI(JSON.stringify($JS$[elem]));\n"
                + "}";

        @Override
        @SuppressWarnings("unchecked")
        public <E> Stub<E> prepare(
                @NonNull ReifiedGeneric<E> target,
                @NonNull QueryJsons annotation,
                @NonNull Parameter p)
                throws BadServiceMappingException
        {
            if (!target.raw().isAssignableFrom(List.class)) {
                throw new TypeRestrictionViolationException(
                        p,
                        QueryJsons.class,
                        TypeRestrictionViolationException.AllowedTypes.LIST,
                        target);
            }
            String paramName = p.getName();
            String choosenName = ObjectUtils.choose(annotation.name(), paramName);
            String js = ObjectUtils.choose(annotation.jsVar(), paramName);

            return new Stub<>(
                    prepareList((ReifiedGeneric) target, annotation, p, choosenName),
                    js,
                    TEMPLATE.replace("$JS$", js).replace("$PN$", choosenName)
            );
        }

        private static <E> ParamProcessor.Worker<List<E>> prepareList(
                @NonNull ReifiedGeneric<List<E>> target,
                @NonNull QueryJsons annotation,
                @NonNull Parameter p,
                @NonNull String paramName)
        {
            return (rq, rp) -> {
                String[] values = rq.queryParamsValues(paramName);
                List<E> elements = new ArrayList<>(values.length);
                for (String s : values) {
                    E elem = JsonTypesProcessor.readJson(
                            annotation.lenient(),
                            Wrappers.unwrapIterable(target),
                            s,
                            x -> new MalformedParameterValueException(p, QueryJsons.class, s, x));
                    elements.add(elem);
                }
                return elements;
            };
        }
    }
}