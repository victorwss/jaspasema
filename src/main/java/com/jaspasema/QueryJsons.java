package br.gov.sp.prefeitura.smit.cgtic.jaspasema;

import br.gov.sp.prefeitura.smit.cgtic.jaspasema.ext.ObjectUtils;
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
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;

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
                @NonNull TargetType<E> target,
                @NonNull QueryJsons annotation,
                @NonNull Parameter p)
                throws BadServiceMappingException
        {
            if (!target.isListType()) throw new BadServiceMappingException(p, "The @QueryJsons should be used only in List types.");
            String paramName = ObjectUtils.choose(annotation.name(), p.getName());
            String js = ObjectUtils.choose(annotation.jsVar(), p.getName());

            return new Stub<>(
                    prepareList((TargetType) target, annotation, p, paramName),
                    js,
                    ""
                            + "for (var elem in " + js + ") {\n"
                            + "    targetUrl += '&" + paramName + "=' + encodeURI(JSON.stringify(" + js + "[elem]));\n"
                            + "}"
            );
        }

        private <E> ParamProcessor.Worker<List<E>> prepareList(
                @NonNull TargetType<List<E>> target,
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
                            TargetType.getListGenericType(target),
                            s);
                    elements.add(elem);
                }
                return elements;
            };
        }
    }
}