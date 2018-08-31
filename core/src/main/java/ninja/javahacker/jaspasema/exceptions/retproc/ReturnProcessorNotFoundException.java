package ninja.javahacker.jaspasema.exceptions.retproc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class ReturnProcessorNotFoundException extends MalformedReturnProcessorException {
    private static final long serialVersionUID = 1L;

    public ReturnProcessorNotFoundException(
            /*@NonNull*/ Method method,
            /*@NonNull*/ Class<? extends Annotation> badAnnotation,
            /*@NonNull*/ Class<?> processorClass)
    {
        super(method, badAnnotation, processorClass);
    }

    public ReturnProcessorNotFoundException(
            /*@NonNull*/ Class<?> definingClass,
            /*@NonNull*/ Class<? extends Annotation> badAnnotation,
            /*@NonNull*/ Class<?> processorClass)
    {
        super(definingClass, badAnnotation, processorClass);
    }
}
