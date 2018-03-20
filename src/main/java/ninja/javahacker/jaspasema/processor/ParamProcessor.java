package ninja.javahacker.jaspasema.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import lombok.NonNull;
import lombok.Value;
import spark.Request;
import spark.Response;

/**
 * @author Victor Williams Stafusa da Silva
 */
public interface ParamProcessor<A extends Annotation> {
    public <E> Stub<E> prepare(
            @NonNull TargetType<E> target,
            @NonNull A annotation,
            @NonNull Parameter p)
            throws BadServiceMappingException;

    @FunctionalInterface
    public interface Worker<E> {
        public E run(
                @NonNull Request rq,
                @NonNull Response rp)
                throws MalformedParameterException;
    }

    @Value
    public class Stub<E> {
        @NonNull
        private Worker<E> worker;

        @NonNull
        private String parameterAdded;

        @NonNull
        private String instructionAdded;
    }

    public static Stub<?> forParameter(Parameter p) throws BadServiceMappingException {
        Annotation interesting = null;
        for (Annotation ann : p.getAnnotations()) {
            if (!ann.annotationType().isAnnotationPresent(ParamSource.class)) continue;
            if (interesting != null) throw new BadServiceMappingException(p, "Conflicting mapping on parameter.");
            interesting = ann;
        }
        if (interesting == null) throw new BadServiceMappingException(p, "No mapping for parameter.");
        return forParameter(p, interesting);
    }

    @SuppressWarnings("unchecked")
    public static <A extends Annotation> Stub<?> forParameter(Parameter p, A interesting) throws BadServiceMappingException {
        ParamProcessor<A> pp;
        try {
            pp = (ParamProcessor<A>) interesting
                    .annotationType()
                    .getAnnotation(ParamSource.class)
                    .processor()
                    .getConstructor()
                    .newInstance();
        } catch (InvocationTargetException e) {
            throw new BadServiceMappingException(p, "Parameter processor could not be created.", e.getCause());
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException e) {
            throw new BadServiceMappingException(p, "Unusable parameter processor.");
        }
        return pp.prepare(TargetType.forType(p.getParameterizedType()), interesting, p);
    }
}
