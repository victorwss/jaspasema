package ninja.javahacker.jaspasema.exceptions.paramproc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class IncompatibleParameterProcessorException extends MalformedParameterProcessorException {
    private static final long serialVersionUID = 1L;

    public IncompatibleParameterProcessorException(
            /*@NonNull*/ Parameter parameter,
            /*@NonNull*/ Class<? extends Annotation> annotation,
            /*@NonNull*/ Class<?> processorClass,
            /*@NonNull*/ Throwable cause)
    {
        super(parameter, annotation, processorClass, cause);
    }
}
