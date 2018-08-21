package ninja.javahacker.jaspasema.exceptions;

import java.lang.annotation.Annotation;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public abstract class MalformedProcessorException extends Exception {
    private static final long serialVersionUID = 1L;

    @NonNull
    private final Class<? extends Annotation> badAnnotation;

    protected MalformedProcessorException(
            /*@NonNull*/ Class<? extends Annotation> badAnnotation,
            /*@NonNull*/ String message,
            /*@NonNull*/ Throwable cause)
    {
        super(message, cause);
        this.badAnnotation = badAnnotation;
    }

    protected MalformedProcessorException(
            /*@NonNull*/ Class<? extends Annotation> badAnnotation,
            /*@NonNull*/ String message)
    {
        this(badAnnotation, message, null);
    }
}
