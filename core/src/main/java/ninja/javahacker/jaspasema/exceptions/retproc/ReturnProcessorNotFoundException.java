package ninja.javahacker.jaspasema.exceptions.retproc;

import java.lang.annotation.Annotation;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class ReturnProcessorNotFoundException extends MalformedReturnProcessorException {
    private static final long serialVersionUID = 1L;

    private static final String TEMPLATE =
            "The annotation @$A$ do not have any @ExitDiscriminator method.";

    protected ReturnProcessorNotFoundException(/*@NonNull*/ Class<? extends Annotation> badAnnotation) {
        super(badAnnotation, TEMPLATE.replace("$A$", badAnnotation.getSimpleName()));
    }

    public static ReturnProcessorNotFoundException create(@NonNull Class<? extends Annotation> badAnnotation) {
        return new ReturnProcessorNotFoundException(badAnnotation);
    }
}
