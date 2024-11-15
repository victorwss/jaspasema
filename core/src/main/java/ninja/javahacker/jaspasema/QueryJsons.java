package ninja.javahacker.jaspasema;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.AllowedTypes;
import ninja.javahacker.jaspasema.exceptions.badmapping.ParameterTypeRestrictionViolationException;
import ninja.javahacker.jaspasema.exceptions.paramvalue.MalformedParameterValueException;
import ninja.javahacker.jaspasema.ext.ObjectUtils;
import ninja.javahacker.jaspasema.processor.AnnotatedParameter;
import ninja.javahacker.jaspasema.processor.JsonTypesProcessor;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import ninja.javahacker.jaspasema.processor.ParamSource;
import ninja.javahacker.reifiedgeneric.Wrappers;

/**
 * @author Victor Williams Stafusa da Silva
 */
@ParamSource(processor = QueryJsons.Processor.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryJsons {

    /**
     * Defines if the JSON should be read in lenient mode or not.
     * In lenient mode, unknown properties in the JSON are simply ignored. In strict mode, their presence triggers an error.
     * <p>The default value is {@code false}. I.e. to NOT be lenient.</p>
     * @return If the JSON should be read in lenient mode or not.
     */
    public boolean lenient() default false;

    /**
     * The name of the query string parameter. Uses the name of the parameter if blank or left unspecified.
     * @return The name of the query string parameter.
     */
    public String name() default "";

    /**
     * Defines the name of the variable used to hold the value of this parameter in the autogenerated javascript stub.
     * If unspecified, this defaults to the parameter name.
     * @return The name of the variable used to hold the value of this parameter in the autogenerated javascript stub.
     */
    public String jsVar() default "";

    /**
     * The class that is responsible for processing the {@link QueryJsons} annotation.
     * @author Victor Williams Stafusa da Silva
     */
    public static class Processor implements ParamProcessor<QueryJsons> {

        private static final String TEMPLATE =
                """
                    for (let __elem in #JS#) {\n
                        __targetUrl += '&#PN#=' + encodeURI(JSON.stringify(#JS#[__elem]));\n
                    };
                """;

        /**
         * Sole constructor.
         */
        public Processor() {
        }

        /**
         * {@inheritDoc}
         * @param <E> {@inheritDoc}
         * @param param {@inheritDoc}
         * @return {@inheritDoc}
         * @throws ParameterTypeRestrictionViolationException If the raw type of the parameter is not {@code java.util.List}.
         */
        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <E> Stub<E> prepare(
                @NonNull AnnotatedParameter<QueryJsons, E> param)
                throws ParameterTypeRestrictionViolationException
        {
            var annotation = param.getAnnotation();
            var paramName = param.getParameterName();
            var target = param.getTarget();
            if (target.getType() != List.class) {
                throw new ParameterTypeRestrictionViolationException(param, AllowedTypes.LIST);
            }
            var choosenName = ObjectUtils.choose(annotation.name(), paramName);
            var js = ObjectUtils.choose(annotation.jsVar(), paramName);
            ParamProcessor.Worker<E> worker = prepareList((AnnotatedParameter) param, choosenName);
            var t1 = TEMPLATE.replace("#JS#", js).replace("#PN#", choosenName);
            return new Stub<>(worker, js, t1);
        }

        @NonNull
        @SuppressFBWarnings("UMTP_UNBOUND_METHOD_TEMPLATE_PARAMETER")
        private <A, B extends List<A>> ParamProcessor.Worker<B> prepareList(
                @NonNull AnnotatedParameter<QueryJsons, B> param,
                @NonNull String paramName)
        {
            return ctx -> {
                var target = param.getTarget();
                var values = ctx.queryParams(paramName);

                @SuppressWarnings("unchecked")
                var elements = (B) new ArrayList<A>(values.size());

                for (var s : values) {
                    var elem = JsonTypesProcessor.readJson(
                            param.getAnnotation().lenient(),
                            Wrappers.unwrapIterable(target),
                            s,
                            MalformedParameterValueException.expectingCause(param, s));
                    elements.add(elem);
                }
                return elements;
            };
        }
    }
}