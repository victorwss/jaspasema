package ninja.javahacker.jaspasema.exceptions.retproc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import ninja.javahacker.jaspasema.processor.ResultProcessor;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class MultipleReturnProcessorsException extends MalformedReturnProcessorException {
    private static final long serialVersionUID = 1L;

    public MultipleReturnProcessorsException(
            /*@NonNull*/ Method method,
            /*@NonNull*/ Class<? extends Annotation> badAnnotation,
            /*@NonNull*/ Class<? extends ResultProcessor<?, ?>> processorClass)
    {
        super(method, badAnnotation, processorClass);
    }

    public MultipleReturnProcessorsException(
            /*@NonNull*/ Class<?> definingClass,
            /*@NonNull*/ Class<? extends Annotation> badAnnotation,
            /*@NonNull*/ Class<? extends ResultProcessor<?, ?>> processorClass)
    {
        super(definingClass, badAnnotation, processorClass);
    }
}
