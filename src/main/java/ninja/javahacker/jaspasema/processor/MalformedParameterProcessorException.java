package ninja.javahacker.jaspasema.processor;

import java.lang.annotation.Annotation;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class MalformedParameterProcessorException extends MalformedProcessorException {
    private static final long serialVersionUID = 1L;

    public MalformedParameterProcessorException(
            /*@NonNull*/ Class<? extends Annotation> badAnnotation,
            /*@NonNull*/ String message,
            /*@NonNull*/ Throwable cause)
    {
        super(badAnnotation, message, cause);
    }
}
