package ninja.javahacker.jaspasema.exceptions.retproc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import ninja.javahacker.jaspasema.processor.ResultProcessor;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class IncompatibleReturnProcessorException extends MalformedReturnProcessorException {
    private static final long serialVersionUID = 1L;

    public IncompatibleReturnProcessorException(
            /*@NonNull*/ Method method,
            /*@NonNull*/ Class<? extends Annotation> annotation,
            /*@NonNull*/ Class<? extends ResultProcessor<?, ?>> processorClass,
            /*@NonNull*/ Throwable cause)
    {
        super(method, annotation, processorClass, cause);
    }

    public IncompatibleReturnProcessorException(
            /*@NonNull*/ Class<?> declaringClass,
            /*@NonNull*/ Class<? extends Annotation> annotation,
            /*@NonNull*/ Class<? extends ResultProcessor<?, ?>> processorClass,
            /*@NonNull*/ Throwable cause)
    {
        super(declaringClass, annotation, processorClass, cause);
    }
}
