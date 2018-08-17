package ninja.javahacker.jaspasema.processor;

import ninja.javahacker.jaspasema.exceptions.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.MalformedReturnValueException;
import ninja.javahacker.jaspasema.exceptions.MalformedReturnProcessorException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.Getter;
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
            if (interesting != null) throw ConflictingMappingOnReturnTypeException.create(m);
            interesting = ann;
        }
        if (interesting == null) throw NoMappingOnReturnTypeException.create(m);
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
            throw ValuedVoidReturnTypeException.create(method, a);
        }
    }

    public static class ConflictingMappingOnReturnTypeException extends BadServiceMappingException {
        private static final long serialVersionUID = 1L;

        public static final String MESSAGE_TEMPLATE = "Conflicting mapping on return type.";

        protected ConflictingMappingOnReturnTypeException(/*@NonNull*/ Method method) {
            super(method, MESSAGE_TEMPLATE);
        }

        public static ConflictingMappingOnReturnTypeException create(@NonNull Method method) {
            return new ConflictingMappingOnReturnTypeException(method);
        }
    }

    public static class NoMappingOnReturnTypeException extends BadServiceMappingException {
        private static final long serialVersionUID = 1L;

        public static final String MESSAGE_TEMPLATE = "No mapping on return type.";

        protected NoMappingOnReturnTypeException(/*@NonNull*/ Method method) {
            super(method, MESSAGE_TEMPLATE);
        }

        public static NoMappingOnReturnTypeException create(@NonNull Method method) {
            return new NoMappingOnReturnTypeException(method);
        }
    }

    @Getter
    public static class ValuedVoidReturnTypeException extends BadServiceMappingException {
        private static final long serialVersionUID = 1L;

        public static final String MESSAGE_TEMPLATE = "Methods returning void should not feature @$A$-annotated annotations.";

        @NonNull
        private final Class<? extends Annotation> annotation;

        protected ValuedVoidReturnTypeException(/*@NonNull*/ Method method, /*@NonNull*/ Class<? extends Annotation> annotation) {
            super(method, MESSAGE_TEMPLATE.replace("$A$", annotation.getSimpleName()));
            this.annotation = annotation;
        }

        public static ValuedVoidReturnTypeException create(@NonNull Method method, @NonNull Class<? extends Annotation> annotation) {
            return new ValuedVoidReturnTypeException(method, annotation);
        }
    }
}
