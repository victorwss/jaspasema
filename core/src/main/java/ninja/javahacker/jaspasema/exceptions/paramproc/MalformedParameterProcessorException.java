package ninja.javahacker.jaspasema.exceptions.paramproc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import ninja.javahacker.jaspasema.exceptions.MalformedProcessorException;

/**
 * @author Victor Williams Stafusa da Silva
 */
public abstract class MalformedParameterProcessorException extends MalformedProcessorException {
    private static final long serialVersionUID = 1L;

    protected MalformedParameterProcessorException(
            /*@NonNull*/ Parameter parameter,
            /*@NonNull*/ Class<? extends Annotation> badAnnotation,
            /*@NonNull*/ Class<?> processorClass,
            /*@NonNull*/ Throwable cause)
    {
        super(parameter, badAnnotation, processorClass, cause);
    }
}
