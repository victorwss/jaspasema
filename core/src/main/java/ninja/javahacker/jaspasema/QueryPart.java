package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.badmapping.TypeRestrictionViolationException;
import ninja.javahacker.jaspasema.ext.ObjectUtils;
import ninja.javahacker.jaspasema.format.ObjectListParser;
import ninja.javahacker.jaspasema.format.ParameterParser;
import ninja.javahacker.jaspasema.format.SimpleParameterType;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import ninja.javahacker.jaspasema.processor.ParamSource;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
@ParamSource(processor = QueryPart.Processor.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryPart {
    public String format() default "";
    public String name() default "";
    public String jsVar() default "";

    public static class Processor implements ParamProcessor<QueryPart> {

        private static final String SINGULAR_JS_TEMPLATE = ""
                + "targetUrl += '&$PARAM$=' + encodeURI($JS$);";

        private static final String PLURAL_JS_TEMPLATE = ""
                + "for (var elem in $JS$) {\n"
                + "    targetUrl += '&$PARAM$=' + encodeURI($JS$[elem]);\n"
                + "}";

        @Override
        public <E> Stub<E> prepare(
                @NonNull ReifiedGeneric<E> target,
                @NonNull QueryPart annotation,
                @NonNull Parameter p)
                throws BadServiceMappingException
        {
            String paramName = ObjectUtils.choose(annotation.name(), p.getName());
            String js = ObjectUtils.choose(annotation.jsVar(), p.getName());

            SimpleParameterType spt = SimpleParameterType.plural(p, target);
            switch (spt) {
                case PLURAL:
                    return new Stub<>(
                            makePluralWork(target, annotation, p),
                            js,
                            PLURAL_JS_TEMPLATE.replace("$JS$", js).replace("$PARAM$", paramName));
                case SINGULAR:
                    ParameterParser<E> part = ParameterParser.prepare(target, annotation.getClass(), annotation.format(), p);
                    return new Stub<>(
                            (rq, rp) -> part.make(rq.queryParams(p.getName())),
                            js,
                            SINGULAR_JS_TEMPLATE.replace("$JS$", js).replace("$PARAM$", paramName));
                case NOT_SIMPLE:
                    throw new TypeRestrictionViolationException(
                            p,
                            QueryPart.class,
                            TypeRestrictionViolationException.AllowedTypes.SIMPLE_AND_LIST,
                            target);
                default:
                    throw new AssertionError(spt);
            }
        }

        @SuppressWarnings("unchecked")
        private static <E> Worker<E> makePluralWork(
                ReifiedGeneric<E> target,
                QueryPart annotation,
                Parameter p)
                throws BadServiceMappingException
        {
            ObjectListParser<E> parts =
                    ObjectListParser.prepare((ReifiedGeneric<List<E>>) target, annotation.getClass(), annotation.format(), p);
            return (rq, rp) -> (E) parts.make(Arrays.asList(rq.queryParamsValues(p.getName())));
        }
    }
}