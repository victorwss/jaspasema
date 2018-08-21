package ninja.javahacker.jaspasema.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.badmapping.ConflictingMappingOnParameterException;
import ninja.javahacker.jaspasema.exceptions.badmapping.NoMappingOnParameterException;
import ninja.javahacker.jaspasema.exceptions.paramproc.IncompatibleParameterProcessorException;
import ninja.javahacker.jaspasema.exceptions.paramproc.MalformedParameterProcessorException;
import ninja.javahacker.jaspasema.exceptions.paramproc.ParameterProcessorConstructorException;
import ninja.javahacker.jaspasema.exceptions.paramproc.UninstantiableParameterProcessorException;
import ninja.javahacker.jaspasema.exceptions.paramvalue.ParameterValueException;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;
import spark.Request;
import spark.Response;

/**
 * @author Victor Williams Stafusa da Silva
 */
public interface ParamProcessor<A extends Annotation> {
    public <E> Stub<E> prepare(
            @NonNull ReifiedGeneric<E> target,
            @NonNull A annotation,
            @NonNull Parameter p)
            throws BadServiceMappingException;

    @FunctionalInterface
    public interface Worker<E> {
        public E run(
                @NonNull Request rq,
                @NonNull Response rp)
                throws ParameterValueException;
    }

    @Value
    @AllArgsConstructor
    public class Stub<E> {
        @NonNull
        private Worker<E> worker;

        @NonNull
        private String parameterAdded;

        @NonNull
        private String instructionAdded;

        @NonNull
        private List<String> preSendInstructionAdded;

        public Stub(
                /*@NonNull*/ Worker<E> worker,
                /*@NonNull*/ String parameterAdded,
                /*@NonNull*/ String instructionAdded,
                /*@NonNull*/ String... preSendInstructionAdded)
        {
            this(worker, parameterAdded, instructionAdded, Arrays.asList(preSendInstructionAdded));
        }
    }

    public static Stub<?> forParameter(
            @NonNull Parameter p)
            throws BadServiceMappingException, MalformedParameterProcessorException
    {
        Annotation interesting = null;
        for (Annotation ann : p.getAnnotations()) {
            if (!ann.annotationType().isAnnotationPresent(ParamSource.class)) continue;
            if (interesting != null) throw ConflictingMappingOnParameterException.create(p);
            interesting = ann;
        }
        if (interesting == null) throw NoMappingOnParameterException.create(p);
        return forParameter(p, interesting);
    }

    @SuppressWarnings("unchecked")
    public static <A extends Annotation> Stub<?> forParameter(
            @NonNull Parameter p,
            @NonNull A interesting)
            throws BadServiceMappingException, MalformedParameterProcessorException
    {
        Class<? extends Annotation> ac = interesting.annotationType();
        Class<? extends ParamProcessor<?>> cc = ac.getAnnotation(ParamSource.class).processor();

        try {
            cc.getMethod("prepare", ReifiedGeneric.class, ac, Parameter.class);
        } catch (NoSuchMethodException e) {
            throw IncompatibleParameterProcessorException.create(interesting.annotationType(), e);
        }

        ParamProcessor<?> pp;
        try {
            pp = cc.getConstructor().newInstance();
        } catch (InvocationTargetException e) {
            throw ParameterProcessorConstructorException.create(interesting.annotationType(), e.getCause());
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException e) {
            throw UninstantiableParameterProcessorException.create(interesting.annotationType(), e);
        }
        return ((ParamProcessor<A>) pp).prepare(ReifiedGeneric.forType(p.getParameterizedType()), interesting, p);
    }
}
