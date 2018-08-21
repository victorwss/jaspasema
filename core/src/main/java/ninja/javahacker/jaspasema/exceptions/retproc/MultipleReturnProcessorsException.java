package ninja.javahacker.jaspasema.exceptions.retproc;

import java.lang.annotation.Annotation;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class MultipleReturnProcessorsException extends MalformedReturnProcessorException {
    private static final long serialVersionUID = 1L;

    private static final String TEMPLATE =
            "The annotation @$A$ should not have more than one @ExitDiscriminator method.";

    protected MultipleReturnProcessorsException(/*@NonNull*/ Class<? extends Annotation> badAnnotation) {
        super(badAnnotation, TEMPLATE.replace("$A$", badAnnotation.getSimpleName()));
    }

    public static MultipleReturnProcessorsException create(@NonNull Class<? extends Annotation> badAnnotation) {
        return new MultipleReturnProcessorsException(badAnnotation);
    }
}
