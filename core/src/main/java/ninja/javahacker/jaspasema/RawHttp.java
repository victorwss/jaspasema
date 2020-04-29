package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.badmapping.TypeRestrictionViolationException;
import ninja.javahacker.jaspasema.processor.AnnotatedParameter;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import ninja.javahacker.jaspasema.processor.ParamSource;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
@ParamSource(processor = RawHttp.Processor.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RawHttp {

    /**
     * The class that is responsible for processing the {@link RawHttp} annotation.
     */
    public static class Processor implements ParamProcessor<RawHttp> {

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
        private static <E> Worker<E> simple(@NonNull AnnotatedParameter<RawHttp, E> param) throws TypeRestrictionViolationException {
            var target = param.getTarget();
            if (target.isSameOf(RQ)) return ctx -> (E) ctx.req;
            if (target.isSameOf(RP)) return ctx -> (E) ctx.res;
            if (target.isSameOf(SS)) return ctx -> (E) ctx.req.getSession(false);
            throw new TypeRestrictionViolationException(param, TypeRestrictionViolationException.AllowedTypes.HTTP);
        }
    }
}