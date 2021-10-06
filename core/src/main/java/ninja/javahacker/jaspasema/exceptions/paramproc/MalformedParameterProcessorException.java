package ninja.javahacker.jaspasema.exceptions.paramproc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import ninja.javahacker.jaspasema.exceptions.MalformedProcessorException;
import ninja.javahacker.jaspasema.processor.ParamProcessor;

/**
 * Raised when a parameter processor can't be instantiated.
 * @see ParamProcessor
 * @author Victor Williams Stafusa da Silva
 */
public abstract class MalformedParameterProcessorException extends MalformedProcessorException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance.
     * @param parameter The method parameter for which the {@link ParamProcessor} couldn't be instantiated.
     * @param badAnnotation The annotation that is processed by the class specified in the {@code processorClass} parameter.
     * @param processorClass Which {@link ParamProcessor} class couldn't be instantiated.
     * @param cause The exception thrown when the {@link ParamProcessor} failed to be instantiated.
     * @throws IllegalArgumentException If any parameter is {@code null}.
     */
    protected MalformedParameterProcessorException(
            /*@NonNull*/ Parameter parameter,
            /*@NonNull*/ Class<? extends Annotation> badAnnotation,
            /*@NonNull*/ Class<? extends ParamProcessor<?>> processorClass,
            /*@NonNull*/ Throwable cause)
    {
        super(parameter, badAnnotation, processorClass, cause);
    }
}
