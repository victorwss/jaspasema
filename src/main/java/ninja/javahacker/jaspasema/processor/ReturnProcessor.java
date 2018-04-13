package ninja.javahacker.jaspasema.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.NonNull;
import lombok.Value;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;
import spark.Request;
import spark.Response;

/**
 * @author Victor Williams Stafusa da Silva
 */
public interface ReturnProcessor<A extends Annotation> {
    public <E> Stub<E> prepare(
            @NonNull ReifiedGeneric<E> target,
            @NonNull A annotation,
            @NonNull Method method)
            throws BadServiceMappingException;

    @FunctionalInterface
    public interface Worker<E> {
        public void run(Request rq, Response rp, E value) throws MalformedReturnValueException;
    }

    @FunctionalInterface
    public interface ProcessorConfiguration {
        public <E> Stub<E> config(
                @NonNull ReifiedGeneric<E> target,
                @NonNull Method method)
                throws BadServiceMappingException;

        public default Stub<?> config(@NonNull Method method) throws BadServiceMappingException {
            return config(ReifiedGeneric.forType(method.getGenericReturnType()), method);
        }
    }

    @Value
    public static class Stub<E> {
        @NonNull
        private Worker<E> worker;

        @NonNull
        private String expectedReturnType;
    }

    public static Stub<?> forMethod(
            @NonNull Method m)
            throws BadServiceMappingException,
            MalformedReturnProcessorException
    {
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
    public static <A extends Annotation> Stub<?> forMethod(
            @NonNull Method method,
            @NonNull A interesting)
            throws BadServiceMappingException,
            MalformedReturnProcessorException
    {
        return prepareConfig(interesting).config(ReifiedGeneric.forType(method.getGenericReturnType()), method);
    }

    public static <T, A extends Annotation> Stub<T> forMethod(
            @NonNull ReifiedGeneric<T> target,
            @NonNull A interesting,
            @NonNull Method method)
            throws BadServiceMappingException,
            MalformedReturnProcessorException
    {
        return prepareConfig(interesting).config(target, method);
    }

    @SuppressWarnings("unchecked")
    public static <A extends Annotation> ProcessorConfiguration prepareConfig(
            @NonNull A interesting)
            throws BadServiceMappingException,
            MalformedReturnProcessorException
    {
        ReturnProcessor<A> pp;
        try {
            pp = (ReturnProcessor<A>) interesting
                    .annotationType()
                    .getAnnotation(ReturnSerializer.class)
                    .processor()
                    .getConstructor()
                    .newInstance();
        } catch (InvocationTargetException e) {
            throw new MalformedReturnProcessorException(
                    interesting.annotationType(),
                    "Return processor could not be created.",
                    e.getCause());
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException e) {
            throw new MalformedReturnProcessorException(
                    interesting.annotationType(),
                    "Unusable return processor.",
                    e);
        }
        return new ProcessorConfiguration() {
            @Override
            public <E> Stub<E> config(ReifiedGeneric<E> target, Method method) throws BadServiceMappingException {
                return pp.prepare(target, interesting, method);
            }
        };
    }

    public static void rejectForVoid(
            @NonNull Method method,
            @NonNull Class<? extends Annotation> a)
            throws BadServiceMappingException
    {
        if (method.getReturnType() == void.class || method.getReturnType() == Void.class) {
            throw new BadServiceMappingException(
                    method,
                    "Methods returning void should not feature @" + a.getSimpleName() + "-annotated annotations.");
        }
    }
}
