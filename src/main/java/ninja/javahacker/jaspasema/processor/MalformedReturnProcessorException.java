package ninja.javahacker.jaspasema.processor;

import java.lang.annotation.Annotation;
import lombok.Getter;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class MalformedReturnProcessorException extends Exception {
    private static final long serialVersionUID = 1L;

    @Getter
    private final Class<? extends Annotation> badAnnotation;

    public MalformedReturnProcessorException(
            /*@NonNull*/ Class<? extends Annotation> badAnnotation,
            /*@NonNull*/ String message,
            /*@NonNull*/ Throwable cause)
    {
        super(message, cause);
        this.badAnnotation = badAnnotation;
    }

    public MalformedReturnProcessorException(
            /*@NonNull*/ Class<? extends Annotation> badAnnotation,
            /*@NonNull*/ String message)
    {
        this(badAnnotation, message, null);
    }
}
