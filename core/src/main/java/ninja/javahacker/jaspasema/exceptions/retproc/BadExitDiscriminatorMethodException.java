package ninja.javahacker.jaspasema.exceptions.retproc;

import java.lang.annotation.Annotation;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class BadExitDiscriminatorMethodException extends MalformedReturnProcessorException {
    private static final long serialVersionUID = 1L;

    private static final String TEMPLATE =
            "The annotation @$A$ have an ill-formed @ExitDiscriminator method.";

    protected BadExitDiscriminatorMethodException(
            /*@NonNull*/ Class<? extends Annotation> badAnnotation)
    {
        super(badAnnotation, TEMPLATE.replace("$A$", badAnnotation.getSimpleName()));
    }

    public static BadExitDiscriminatorMethodException create(@NonNull Class<? extends Annotation> badAnnotation) {
        return new BadExitDiscriminatorMethodException(badAnnotation);
    }
}
