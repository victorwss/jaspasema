package ninja.javahacker.jaspasema.exceptions.paramproc;

import java.lang.annotation.Annotation;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class ParameterProcessorConstructorException extends MalformedParameterProcessorException {
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_TEMPLATE =
            "Parameter processor constructor of class $R$ throwed an exception.";

    protected ParameterProcessorConstructorException(
            /*@NonNull*/ Class<? extends Annotation> annotation,
            /*@NonNull*/ Throwable cause)
    {
        super(annotation, MESSAGE_TEMPLATE.replace("$R$", annotation.getSimpleName()), cause);
    }

    public static ParameterProcessorConstructorException create(
            @NonNull Class<? extends Annotation> annotation,
            @NonNull Throwable cause)
    {
        return new ParameterProcessorConstructorException(annotation, cause);
    }
}
