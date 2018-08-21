package ninja.javahacker.jaspasema.exceptions.paramproc;

import java.lang.annotation.Annotation;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class UninstantiableParameterProcessorException extends MalformedParameterProcessorException {
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_TEMPLATE =
            "The parameter processor $R$ is not an instantiable class.";

    protected UninstantiableParameterProcessorException(
            /*@NonNull*/ Class<? extends Annotation> annotation,
            /*@NonNull*/ Throwable cause)
    {
        super(annotation, MESSAGE_TEMPLATE.replace("$R$", annotation.getSimpleName()), cause);
    }

    public static UninstantiableParameterProcessorException create(
            @NonNull Class<? extends Annotation> annotation,
            @NonNull Throwable cause)
    {
        return new UninstantiableParameterProcessorException(annotation, cause);
    }
}
