package ninja.javahacker.jaspasema.exceptions;

import java.lang.annotation.Annotation;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class MalformedReturnProcessorException extends MalformedProcessorException {
    private static final long serialVersionUID = 1L;

    public MalformedReturnProcessorException(
            /*@NonNull*/ Class<? extends Annotation> badAnnotation,
            /*@NonNull*/ String message,
            /*@NonNull*/ Throwable cause)
    {
        super(badAnnotation, message, cause);
    }

    public MalformedReturnProcessorException(
            /*@NonNull*/ Class<? extends Annotation> badAnnotation,
            /*@NonNull*/ String message)
    {
        this(badAnnotation, message, null);
    }
}
