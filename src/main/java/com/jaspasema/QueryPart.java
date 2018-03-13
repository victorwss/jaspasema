package br.gov.sp.prefeitura.smit.cgtic.jaspasema;

import br.gov.sp.prefeitura.smit.cgtic.jaspasema.ext.ObjectUtils;
import br.gov.sp.prefeitura.smit.cgtic.jaspasema.format.ObjectListParser;
import br.gov.sp.prefeitura.smit.cgtic.jaspasema.format.ObjectParser;
import br.gov.sp.prefeitura.smit.cgtic.jaspasema.format.SimpleParameterType;
import br.gov.sp.prefeitura.smit.cgtic.jaspasema.processor.BadServiceMappingException;
import br.gov.sp.prefeitura.smit.cgtic.jaspasema.processor.ParamProcessor;
import br.gov.sp.prefeitura.smit.cgtic.jaspasema.processor.ParamSource;
import br.gov.sp.prefeitura.smit.cgtic.jaspasema.processor.TargetType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import lombok.NonNull;

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

        private static final String NOT_SIMPLE_ERROR_MESSAGE = ""
                + "The @QueryPart annotation must be used only on parameters of primitives, "
                + "primitive wrappers, String and date/time types and Lists of those.";

        @Override
        @SuppressWarnings("unchecked")
        public <E> Stub<E> prepare(
                @NonNull TargetType<E> target,
                @NonNull QueryPart annotation,
                @NonNull Parameter p)
                throws BadServiceMappingException
        {
            String paramName = ObjectUtils.choose(annotation.name(), p.getName());
            String js = ObjectUtils.choose(annotation.jsVar(), p.getName());

            switch (SimpleParameterType.plural(p, target)) {
                case PLURAL:
                    ObjectListParser<E> parts =
                            ObjectListParser.prepare((TargetType) target, annotation.getClass(), annotation.format(), p);
                    return new Stub<>(
                            (rq, rp) -> (E) parts.make(Arrays.asList(rq.queryParamsValues(p.getName()))),
                            js,
                            ""
                                    + "for (var elem in " + js + ") {\n"
                                    + "    targetUrl += '&" + paramName + "=' + encodeURI(" + js + "[elem]);\n"
                                    + "}"
                    );
                case SINGULAR:
                    ObjectParser<E> part = ObjectParser.prepare(target, annotation.getClass(), annotation.format(), p);
                    return new Stub<>(
                            (rq, rp) -> part.make(rq.queryParams(p.getName())),
                            js,
                            "targetUrl += '&" + paramName + "=' + encodeURI(" + js + ");");
                case NOT_SIMPLE:
                    throw new BadServiceMappingException(p, NOT_SIMPLE_ERROR_MESSAGE);
                default:
                    throw new AssertionError();
            }
        }
    }
}