package ninja.javahacker.jaspasema.exceptions.paramproc;

import java.lang.annotation.Annotation;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class IncompatibleParameterProcessorException extends MalformedParameterProcessorException {
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_TEMPLATE =
            "The parameter processor assigned for annotation $R$ do not understands it.";

    protected IncompatibleParameterProcessorException(
            /*@NonNull*/ Class<? extends Annotation> annotation,
            /*@NonNull*/ Throwable cause)
    {
        super(annotation, MESSAGE_TEMPLATE.replace("$R$", annotation.getSimpleName()), cause);
    }

    public static IncompatibleParameterProcessorException create(
            @NonNull Class<? extends Annotation> annotation,
            @NonNull Throwable cause)
    {
        return new IncompatibleParameterProcessorException(annotation, cause);
    }
}
