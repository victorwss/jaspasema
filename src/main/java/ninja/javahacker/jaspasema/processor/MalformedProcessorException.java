package ninja.javahacker.jaspasema.processor;

import java.lang.annotation.Annotation;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class MalformedProcessorException extends Exception {
    private static final long serialVersionUID = 1L;

    @NonNull
    private final Class<? extends Annotation> badAnnotation;

    public MalformedProcessorException(
            /*@NonNull*/ Class<? extends Annotation> badAnnotation,
            /*@NonNull*/ String message,
            /*@NonNull*/ Throwable cause)
    {
        super(message, cause);
        this.badAnnotation = badAnnotation;
    }

    public MalformedProcessorException(
            /*@NonNull*/ Class<? extends Annotation> badAnnotation,
            /*@NonNull*/ String message)
    {
        this(badAnnotation, message, null);
    }
}
