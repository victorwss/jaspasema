package ninja.javahacker.jaspasema;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.AllowedTypes;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.badmapping.ParameterTypeRestrictionViolationException;
import ninja.javahacker.jaspasema.processor.AnnotatedParameter;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import ninja.javahacker.jaspasema.processor.ParamSource;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * Denotes that the value of a method parameter should be the base
 * {@link HttpServletRequest}, {@link HttpServletResponse} or {@link HttpSession}.
 *
 * <p>For example:</p>
 * <pre>
 *     &#64;Get
 *     &#64;Path("/foo")
 *     public String foo(
 *         &#64;HeaderParam String bar,               // Uses the content of the "bar" header.
 *         &#64;RawHttp HttpServletRequest request,   // The request.
 *         &#64;RawHttp HttpServletResponse response, // The response.
 *         &#64;RawHttp HttpSession session,          // The session.
 *     {
 *         // Do stuff.
 *     }
 * </pre>
 * @author Victor Williams Stafusa da Silva
 */
@ParamSource(processor = RawHttp.Processor.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RawHttp {

    /**
     * The class that is responsible for processing the {@link RawHttp} annotation.
     * @author Victor Williams Stafusa da Silva
     */
    public static final class Processor implements ParamProcessor<RawHttp> {

        private static final ReifiedGeneric<HttpServletRequest> RQ = ReifiedGeneric.of(HttpServletRequest.class);
        private static final ReifiedGeneric<HttpServletResponse> RP = ReifiedGeneric.of(HttpServletResponse.class);
        private static final ReifiedGeneric<HttpSession> SS = ReifiedGeneric.of(HttpSession.class);

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
        public <E> Stub<E> prepare(@NonNull AnnotatedParameter<RawHttp, E> param) throws BadServiceMappingException {
            return new Stub<>(simple(param), "", "");
        }

        @NonNull
        @SuppressWarnings("unchecked")
        private static <E> Worker<E> simple(@NonNull AnnotatedParameter<RawHttp, E> param)
                throws ParameterTypeRestrictionViolationException
        {
            var target = param.getTarget();
            if (target.isSameOf(RQ)) return ctx -> (E) ctx.req();
            if (target.isSameOf(RP)) return ctx -> (E) ctx.res();
            if (target.isSameOf(SS)) return ctx -> (E) ctx.req().getSession(false);
            throw new ParameterTypeRestrictionViolationException(param, AllowedTypes.HTTP);
        }
    }
}