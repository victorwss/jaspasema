package ninja.javahacker.jaspasema.exceptions.paramproc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import ninja.javahacker.jaspasema.processor.ParamProcessor;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class ParameterProcessorConstructorException extends MalformedParameterProcessorException {
    private static final long serialVersionUID = 1L;

    public ParameterProcessorConstructorException(
            /*@NonNull*/ Parameter parameter,
            /*@NonNull*/ Class<? extends Annotation> annotation,
            /*@NonNull*/ Class<? extends ParamProcessor<?>> processorClass,
            /*@NonNull*/ Throwable cause)
    {
        super(parameter, annotation, processorClass, cause);
    }
}
