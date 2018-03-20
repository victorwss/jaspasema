package ninja.javahacker.jaspasema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.processor.BadServiceMappingException;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import ninja.javahacker.jaspasema.processor.ParamSource;
import ninja.javahacker.jaspasema.processor.TargetType;
import spark.Request;
import spark.Response;
import spark.Session;

/**
 * @author Victor Williams Stafusa da Silva
 */
@ParamSource(processor = RawHttp.Processor.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RawHttp {
    public String format() default "";

    public static class Processor implements ParamProcessor<RawHttp> {

        private static final TargetType<Request> RQ = TargetType.forClass(Request.class);
        private static final TargetType<Response> RP = TargetType.forClass(Response.class);
        private static final TargetType<Session> SS = TargetType.forClass(Session.class);

        @Override
        public <E> Stub<E> prepare(
                @NonNull TargetType<E> target,
                @NonNull RawHttp annotation,
                @NonNull Parameter p)
                throws BadServiceMappingException
        {
            return new Stub<>(simple(target, p), "", "");
        }

        @SuppressWarnings("unchecked")
        private <E> Worker<E> simple(
                @NonNull TargetType<E> target,
                @NonNull Parameter p)
                throws BadServiceMappingException
        {
            if (target.isSameOf(RQ)) return (rq, rp) -> (E) rq;
            if (target.isSameOf(RP)) return (rq, rp) -> (E) rp;
            if (target.isSameOf(SS)) return (rq, rp) -> (E) rq.session(false);
            throw new BadServiceMappingException(p,
                    "The @RawHttp annotation should be used only in Request, Response or Session parameters.");
        }
    }
}