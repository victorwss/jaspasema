package ninja.javahacker.jaspasema.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.NonNull;
import lombok.Value;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.badmapping.ConflictingMappingOnReturnTypeException;
import ninja.javahacker.jaspasema.exceptions.badmapping.NoMappingOnReturnTypeException;
import ninja.javahacker.jaspasema.exceptions.badmapping.VoidWithValueReturnTypeException;
import ninja.javahacker.jaspasema.exceptions.retproc.IncompatibleReturnProcessorException;
import ninja.javahacker.jaspasema.exceptions.retproc.MalformedReturnProcessorException;
import ninja.javahacker.jaspasema.exceptions.retproc.ReturnProcessorConstructorException;
import ninja.javahacker.jaspasema.exceptions.retproc.UninstantiableReturnProcessorException;
import ninja.javahacker.jaspasema.exceptions.retvalue.MalformedReturnValueException;
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
        Class<? extends Annotation> ac = interesting.annotationType();
        Class<? extends ReturnProcessor<?>> cc = ac.getAnnotation(ReturnSerializer.class).processor();

        try {
            cc.getMethod("prepare", ReifiedGeneric.class, ac, Method.class);
        } catch (NoSuchMethodException e) {
            throw IncompatibleReturnProcessorException.create(interesting.annotationType(), e);
        }

        ReturnProcessor<A> pp;
        try {
            pp = (ReturnProcessor<A>) interesting
                    .annotationType()
                    .getAnnotation(ReturnSerializer.class)
                    .processor()
                    .getConstructor()
                    .newInstance();
        } catch (InvocationTargetException e) {
            throw ReturnProcessorConstructorException.create(interesting.annotationType(), e.getCause());
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException e) {
            throw UninstantiableReturnProcessorException.create(interesting.annotationType(), e);
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
            throw VoidWithValueReturnTypeException.create(method, a);
        }
    }
}
