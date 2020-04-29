package ninja.javahacker.jaspasema.exceptions.retproc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class ReturnProcessorConstructorException extends MalformedReturnProcessorException {
    private static final long serialVersionUID = 1L;

    public ReturnProcessorConstructorException(
            /*@NonNull*/ Method method,
            /*@NonNull*/ Class<? extends Annotation> annotation,
            /*@NonNull*/ Class<?> processorClass,
            /*@NonNull*/ Throwable cause)
    {
        super(method, annotation, processorClass, cause);
    }

    public ReturnProcessorConstructorException(
            /*@NonNull*/ Class<?> declaringClass,
            /*@NonNull*/ Class<? extends Annotation> annotation,
            /*@NonNull*/ Class<?> processorClass,
            /*@NonNull*/ Throwable cause)
    {
        super(declaringClass, annotation, processorClass, cause);
    }
}
