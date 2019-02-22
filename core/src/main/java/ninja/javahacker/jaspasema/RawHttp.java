package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.badmapping.TypeRestrictionViolationException;
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
    public String format() default "";

    public static class Processor implements ParamProcessor<RawHttp> {

        private static final ReifiedGeneric<HttpServletRequest> RQ = ReifiedGeneric.of(HttpServletRequest.class);
        private static final ReifiedGeneric<HttpServletResponse> RP = ReifiedGeneric.of(HttpServletResponse.class);
        private static final ReifiedGeneric<HttpSession> SS = ReifiedGeneric.of(HttpSession.class);

        @Override
        public <E> Stub<E> prepare(
                @NonNull ReifiedGeneric<E> target,
                @NonNull RawHttp annotation,
                @NonNull Parameter p)
                throws BadServiceMappingException
        {
            return new Stub<>(simple(target, p), "", "");
        }

        @SuppressWarnings("unchecked")
        private static <E> Worker<E> simple(
                @NonNull ReifiedGeneric<E> target,
                @NonNull Parameter p)
                throws BadServiceMappingException
        {
            if (target.isSameOf(RQ)) return ctx -> (E) ctx.req;
            if (target.isSameOf(RP)) return ctx -> (E) ctx.res;
            if (target.isSameOf(SS)) return ctx -> (E) ctx.req.getSession(false);

            throw new TypeRestrictionViolationException(
                    p,
                    RawHttp.class,
                    TypeRestrictionViolationException.AllowedTypes.HTTP,
                    target);
        }
    }
}