package ninja.javahacker.jaspasema.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.NonNull;
import lombok.Value;

/**
 * @author Victor Williams Stafusa da Silva
 */
public interface ReturnProcessor<A extends Annotation> {
    public <E> Stub<E> prepare(
            @NonNull TargetType<E> target,
            @NonNull A annotation,
            @NonNull Method method)
            throws BadServiceMappingException;

    @FunctionalInterface
    public interface Worker<E> {
        public String run(E value) throws InvocationTargetException;
    }

    @Value
    public class Stub<E> {
        @NonNull
        private Worker<E> worker;

        @NonNull
        private String expectedReturnType;
    }

    public static Stub<?> forMethod(@NonNull Method m) throws BadServiceMappingException {
        Annotation interesting = null;
        for (Annotation ann : m.getAnnotations()) {
            if (!ann.annotationType().isAnnotationPresent(ReturnSerializer.class)) continue;
            if (interesting != null) throw new BadServiceMappingException(m, "Conflicting mapping on return type.");
            interesting = ann;
        }
        if (interesting == null) throw new BadServiceMappingException(m, "No mapping for return type.");
        return forMethod(m, interesting);
    }

    @SuppressWarnings("unchecked")
    public static <A extends Annotation> Stub<?> forMethod(@NonNull Method m, @NonNull A interesting) throws BadServiceMappingException {
        ReturnProcessor<A> pp;
        try {
            pp = (ReturnProcessor<A>) interesting
                    .annotationType()
                    .getAnnotation(ReturnSerializer.class)
                    .processor()
                    .getConstructor()
                    .newInstance();
        } catch (InvocationTargetException e) {
            throw new BadServiceMappingException(m, "Return processor could not be created.", e.getCause());
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException e) {
            throw new BadServiceMappingException(m, "Unusable return processor.");
        }
        return pp.prepare(TargetType.forType(m.getGenericReturnType()), interesting, m);
    }
}