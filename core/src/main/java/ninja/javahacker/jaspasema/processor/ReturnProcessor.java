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
import ninja.javahacker.jaspasema.exceptions.retproc.MalformedReturnProcessorException;
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
        public void run(
                @NonNull Method method,
                @NonNull Request rq,
                @NonNull Response rp,
                @NonNull E value)
                throws MalformedReturnValueException;
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
            if (interesting != null) throw new ConflictingMappingOnReturnTypeException(m);
            interesting = ann;
        }
        if (interesting == null) throw new NoMappingOnReturnTypeException(m);
        return forMethod(m, interesting);
    }

    @SuppressWarnings("unchecked")
    public static <A extends Annotation> Stub<?> forMethod(
            @NonNull Method method,
            @NonNull A interesting)
            throws BadServiceMappingException,
            MalformedReturnProcessorException
    {
        return prepareConfig(interesting, MalformedReturnProcessorException.onMethod(method)).config(method);
    }

    @SuppressWarnings("unchecked")
    public static <A extends Annotation> ProcessorConfiguration prepareConfig(
            @NonNull A interesting,
            @NonNull MalformedReturnProcessorException.Factory x)
            throws BadServiceMappingException,
            MalformedReturnProcessorException
    {
        Class<? extends Annotation> ac = interesting.annotationType();
        ReturnSerializer r = ac.getAnnotation(ReturnSerializer.class);
        if (r == null) throw new IllegalArgumentException();
        Class<? extends ReturnProcessor<?>> cc = r.processor();

        try {
            cc.getMethod("prepare", ReifiedGeneric.class, ac, Method.class);
        } catch (NoSuchMethodException e) {
            throw x.incompatible(ac, cc, e);
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
            throw x.exception(ac, cc, e.getCause());
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException e) {
            throw x.uninstantiable(ac, cc, e);
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
            throw new VoidWithValueReturnTypeException(method, a);
        }
    }
}
