package ninja.javahacker.jaspasema.exceptions.retproc;

import java.lang.annotation.Annotation;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class UninstantiableReturnProcessorException extends MalformedReturnProcessorException {
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_TEMPLATE =
            "The return processor $R$ is not an instantiable class.";

    protected UninstantiableReturnProcessorException(
            /*@NonNull*/ Class<? extends Annotation> annotation,
            /*@NonNull*/ Throwable cause)
    {
        super(annotation, MESSAGE_TEMPLATE.replace("$R$", annotation.getSimpleName()), cause);
    }

    public static UninstantiableReturnProcessorException create(
            @NonNull Class<? extends Annotation> annotation,
            @NonNull Throwable cause)
    {
        return new UninstantiableReturnProcessorException(annotation, cause);
    }
}
