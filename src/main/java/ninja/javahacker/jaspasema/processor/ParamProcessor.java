package ninja.javahacker.jaspasema.processor;

import ninja.javahacker.jaspasema.exceptions.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.MalformedParameterProcessorException;
import ninja.javahacker.jaspasema.exceptions.ParameterValueException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
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

    public static Stub<?> forParameter(Parameter p) throws BadServiceMappingException, MalformedParameterProcessorException {
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
    public static <A extends Annotation> Stub<?> forParameter(Parameter p, A interesting)
            throws BadServiceMappingException, MalformedParameterProcessorException
    {
        ParamProcessor<A> pp;
        try {
            pp = (ParamProcessor<A>) interesting
                    .annotationType()
                    .getAnnotation(ParamSource.class)
                    .processor()
                    .getConstructor()
                    .newInstance();
        } catch (InvocationTargetException e) {
            throw new MalformedParameterProcessorException(
                    interesting.annotationType(),
                    "Parameter processor could not be created.",
                    e.getCause());
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException e) {
            throw new MalformedParameterProcessorException(
                    interesting.annotationType(),
                    "Unusable parameter processor.",
                    e);
        }
        return pp.prepare(ReifiedGeneric.forType(p.getParameterizedType()), interesting, p);
    }

    public static class ConflictingMappingOnParameterException extends BadServiceMappingException {
        private static final long serialVersionUID = 1L;

        public static final String MESSAGE_TEMPLATE = "Conflicting mapping on parameter.";

        protected ConflictingMappingOnParameterException(/*@NonNull*/ Parameter parameter) {
            super(parameter, MESSAGE_TEMPLATE);
        }

        public static ConflictingMappingOnParameterException create(@NonNull Parameter parameter) {
            return new ConflictingMappingOnParameterException(parameter);
        }
    }

    public static class NoMappingOnParameterException extends BadServiceMappingException {
        private static final long serialVersionUID = 1L;

        public static final String MESSAGE_TEMPLATE = "No mapping on parameter.";

        protected NoMappingOnParameterException(/*@NonNull*/ Parameter parameter) {
            super(parameter, MESSAGE_TEMPLATE);
        }

        public static NoMappingOnParameterException create(@NonNull Parameter parameter) {
            return new NoMappingOnParameterException(parameter);
        }
    }
}
