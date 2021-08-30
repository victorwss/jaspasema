package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import lombok.NonNull;
import ninja.javahacker.jaspasema.ext.ObjectUtils;
import ninja.javahacker.jaspasema.processor.AnnotatedParameter;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import ninja.javahacker.jaspasema.processor.ParamSource;

/**
 * Denotes that the value of a method parameter should be read from the session.
 *
 * <p>For example:</p>
 * <pre>
 *     public String foo(
 *         &#64;HeaderParam(name = "user-agent") String userAgent, // Uses the content of the "user-agent" header.
 *         &#64;SessionParam String bar,                           // Reads the bar from the session's "bar".
 *         &#64;SessionParam LocalDate loginDate,                  // Reads the loginDate from the session's "loginDate".
 *         &#64;SessionParam(name = "-^what") Fruit fruit)         // Reads a fruit object from the session's "-^what".
 *     {
 *         // Do stuff.
 *     }
 * </pre>
 * @author Victor Williams Stafusa da Silva
 */
@ParamSource(processor = SessionParam.Processor.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface SessionParam {

    /**
     * The name of the session attribute. Uses the name of the parameter if blank or left unspecified.
     * @return The name of the cookie.
     */
    public String name() default "";

    /**
     * The class that is responsible for processing the {@link SessionParam} annotation.
     * @author Victor Williams Stafusa da Silva
     */
    public static class Processor implements ParamProcessor<SessionParam> {

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
         */
        @NonNull
        @Override
        public <E> Stub<E> prepare(@NonNull AnnotatedParameter<SessionParam, E> param) {
            var annotation = param.getAnnotation();
            var paramName = ObjectUtils.choose(annotation.name(), param.getParameterName());
            return new Stub<>(ctx -> ctx.sessionAttribute(paramName), "", "");
        }
    }
}