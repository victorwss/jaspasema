package ninja.javahacker.jaspasema.exceptions.paramproc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import ninja.javahacker.jaspasema.processor.ParamProcessor;

/**
 * Thrown when a {@link ParamProcessor} couldn't be instantiated because its class somehow lacks the
 * {@link ParamProcessor#prepare(AnnotatedParameter)} method.
 * @author Victor Williams Stafusa da Silva
 */
public class IncompatibleParameterProcessorException extends MalformedParameterProcessorException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance.
     * @param parameter The method parameter for which the {@link ParamProcessor} couldn't be instantiated.
     * @param badAnnotation The annotation that is processed by the class specified in the {@code processorClass} parameter.
     * @param processorClass Which {@link ParamProcessor} class couldn't be instantiated.
     * @param cause The exception thrown when the {@link ParamProcessor} failed to be instantiated.
     * @throws IllegalArgumentException If {@code parameter} is {@code null}.
     */
    public IncompatibleParameterProcessorException(
            /*@NonNull*/ Parameter parameter,
            /*@NonNull*/ Class<? extends Annotation> badAnnotation,
            /*@NonNull*/ Class<? extends ParamProcessor<?>> processorClass,
            /*@NonNull*/ Throwable cause)
    {
        super(parameter, badAnnotation, processorClass, cause);
    }
}
