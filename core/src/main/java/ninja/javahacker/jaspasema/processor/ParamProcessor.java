package ninja.javahacker.jaspasema.processor;

import edu.umd.cs.findbugs.annotations.Nullable;
import io.javalin.http.Context;
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

/**
 * Defines parameter processors which deserializes method parameters exposing them to be called from HTTP requests.
 * Each implementing class should feature a no-arg public parameter and is tied to a particular annotation via the
 * {@link ParamSource} annotation.
 * @param <A> The type of the annotation that is accepted by an implementation of this interface.
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface ParamProcessor<A extends Annotation> {

    /**
     * Defines how an annotated parameter is processed. The parameter's data is hold on an {@link AnnotatedParameter} object.
     * This method makes static verifications of the parameter usage (like if it is used on a suitable parameter type),
     * and it returns a {@link Stub} which is responsible for the run-time verification (i.e. each time the method with the
     * annotated parameter is actually called).
     * @param <E> The type of the annotated parameter.
     * @param param Object holding the parameter data.
     * @return A {@link Stub} which is responsible for the run-time verification (i.e. each time the method with the
     *         annotated parameter is actually called).
     * @throws BadServiceMappingException If the static verifications of the parameter usage are found to be invalid.
     *         Specific subclasses of {@link BadServiceMappingException} will be used for each particular case.
     */
    @NonNull
    public <E> Stub<E> prepare(@NonNull AnnotatedParameter<A, E> param) throws BadServiceMappingException;

    @FunctionalInterface
    public interface Worker<E> {
        @Nullable
        public E run(@NonNull Context ctx) throws ParameterValueException;
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
                /*@NonNull*/ String... preSendInstructionsAdded)
        {
            this(worker, parameterAdded, instructionAdded, Arrays.asList(preSendInstructionsAdded));
        }
    }

    @NonNull
    public static Stub<?> forParameter(
            @NonNull Parameter p)
            throws BadServiceMappingException, MalformedParameterProcessorException
    {
        Annotation interesting = null;
        for (var ann : p.getAnnotations()) {
            if (!ann.annotationType().isAnnotationPresent(ParamSource.class)) continue;
            if (interesting != null) throw new ConflictingMappingOnParameterException(p);
            interesting = ann;
        }
        if (interesting == null) throw new NoMappingOnParameterException(p);
        return forParameter(AnnotatedParameter.of(interesting, p));
    }

    @NonNull
    @SuppressWarnings("unchecked")
    private static <A extends Annotation, E> Stub<E> forParameter(
            @NonNull AnnotatedParameter<A, E> param)
            throws BadServiceMappingException, MalformedParameterProcessorException
    {
        var interesting = param.getAnnotation();
        var ac = interesting.annotationType();
        var cc = ac.getAnnotation(ParamSource.class).processor();
        var p = param.getParameter();

        try {
            cc.getMethod("prepare", AnnotatedParameter.class);
        } catch (NoSuchMethodException e) {
            throw new IncompatibleParameterProcessorException(p, interesting.annotationType(), cc, e);
        }

        ParamProcessor<A> pp;
        try {
            pp = (ParamProcessor<A>) cc.getConstructor().newInstance();
        } catch (InvocationTargetException e) {
            throw new ParameterProcessorConstructorException(p, interesting.annotationType(), cc, e.getCause());
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException e) {
            throw new UninstantiableParameterProcessorException(p, interesting.annotationType(), cc, e);
        }
        return pp.prepare(param);
    }
}
