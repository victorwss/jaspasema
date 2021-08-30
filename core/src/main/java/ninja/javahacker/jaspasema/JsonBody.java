package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.paramvalue.MalformedParameterValueException;
import ninja.javahacker.jaspasema.processor.AnnotatedParameter;
import ninja.javahacker.jaspasema.processor.JsonTypesProcessor;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import ninja.javahacker.jaspasema.processor.ParamSource;

/**
 * Denotes that the value of a method parameter should be read from the request body as
 * a JSON and the value desserialized as an object from that JSON.
 *
 * <p>For example:</p>
 * <pre>
 *     &#64;Post
 *     &#64;Path("/foo")
 *     public String foo(
 *         // Uses the content of the "f1" header, reads it as a JSON and deserializes a Fruit instance.
 *         &#64;HeaderJsonParam Fruit f1,
 *         // Uses the content of the JSON body, reads it as a JSON and deserializes a Flavor instance.
 *         &#64;JsonBody Flavor f2)
 *     {
 *         // Do stuff.
 *     }
 * </pre>
 * @author Victor Williams Stafusa da Silva
 */
@ParamSource(processor = JsonBody.Processor.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonBody {

    /**
     * Defines if the JSON should be read in lenient mode or not.
     * In lenient mode, unknown properties in the JSON are simply ignored. In strict mode, their presence triggers an error.
     * <p>The default value is {@code false}. I.e. to NOT be lenient.</p>
     * @return If the JSON should be read in lenient mode or not.
     */
    public boolean lenient() default false;

    /**
     * The class that is responsible for processing the {@link JsonBody} annotation.
     */
    public static class Processor implements ParamProcessor<JsonBody> {

        private static final String REQUEST_TYPE = "requestType = 'application/json; charset=utf-8';";

        private static final String JSONIFY = "__data = JSON.stringify(__data);";

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
         * @throws BadServiceMappingException {@inheritDoc}
         */
        @NonNull
        @Override
        public <E> Stub<E> prepare(@NonNull AnnotatedParameter<JsonBody, E> param) throws BadServiceMappingException {
            ParamProcessor.Worker<E> w = ctx -> {
                var s = ctx.body();
                var m = MalformedParameterValueException.expectingCause(param, s);
                return JsonTypesProcessor.readJson(param.getAnnotation().lenient(), param.getTarget(), s, m);
            };
            return new Stub<>(w, "", "", REQUEST_TYPE, JSONIFY);
        }
    }
}