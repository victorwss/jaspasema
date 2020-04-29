package ninja.javahacker.jaspasema.processor;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.javalin.http.Context;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.NonNull;
import lombok.Value;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.badmapping.VoidWithValueReturnTypeException;
import ninja.javahacker.jaspasema.exceptions.retproc.MalformedReturnProcessorException;
import ninja.javahacker.jaspasema.exceptions.retvalue.MalformedReturnValueException;

/**
 * @author Victor Williams Stafusa da Silva
 */
public interface ResultProcessor<A extends Annotation, B> {
    @NonNull
    public <E extends B> Stub<E> prepare(@NonNull AnnotatedMethod<A, E> meth) throws BadServiceMappingException;

    @FunctionalInterface
    public interface Worker<E> {
        public void run(
                @NonNull Method method,
                @NonNull Context ctx,
                @Nullable E value)
                throws MalformedReturnValueException;
    }

    @Value
    public static class Stub<E> {
        @NonNull
        private Worker<E> worker;

        @NonNull
        private String expectedReturnType;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    @SuppressFBWarnings("LEST_LOST_EXCEPTION_STACK_TRACE")
    public static <A extends Annotation, E> Stub<E> prepareConfig(
            @NonNull AnnotatedMethod<A, E> meth,
            @NonNull MalformedReturnProcessorException.Factory x)
            throws BadServiceMappingException,
            MalformedReturnProcessorException
    {
        var interesting = meth.getAnnotation();
        var ac = interesting.annotationType();
        var r = ac.getAnnotation(ResultSerializer.class);
        if (r == null) throw new IllegalArgumentException();
        var cc = r.processor();

        try {
            cc.getMethod("prepare", AnnotatedMethod.class);
        } catch (NoSuchMethodException e) {
            throw x.incompatible(ac, cc, e);
        }

        ResultProcessor<A, E> pp;
        try {
            pp = (ResultProcessor<A, E>) cc.getConstructor().newInstance();
        } catch (InvocationTargetException e) {
            throw x.exception(ac, cc, e.getCause());
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException e) {
            throw x.uninstantiable(ac, cc, e);
        }
        return pp.prepare(meth);
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
