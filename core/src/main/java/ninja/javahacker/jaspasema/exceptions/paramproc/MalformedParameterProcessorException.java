package ninja.javahacker.jaspasema.exceptions.paramproc;

import java.lang.annotation.Annotation;
import lombok.Getter;
import ninja.javahacker.jaspasema.exceptions.MalformedProcessorException;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public abstract class MalformedParameterProcessorException extends MalformedProcessorException {
    private static final long serialVersionUID = 1L;

    protected MalformedParameterProcessorException(
            /*@NonNull*/ Class<? extends Annotation> badAnnotation,
            /*@NonNull*/ String message,
            /*@NonNull*/ Throwable cause)
    {
        super(badAnnotation, message, cause);
    }
}
