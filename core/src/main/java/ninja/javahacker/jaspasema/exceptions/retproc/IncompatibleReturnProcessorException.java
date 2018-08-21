package ninja.javahacker.jaspasema.exceptions.retproc;

import java.lang.annotation.Annotation;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class IncompatibleReturnProcessorException extends MalformedReturnProcessorException {
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_TEMPLATE =
            "The return processor assigned for annotation $R$ do not understands it.";

    protected IncompatibleReturnProcessorException(
            /*@NonNull*/ Class<? extends Annotation> annotation,
            /*@NonNull*/ Throwable cause)
    {
        super(annotation, MESSAGE_TEMPLATE.replace("$R$", annotation.getSimpleName()), cause);
    }

    public static IncompatibleReturnProcessorException create(
            @NonNull Class<? extends Annotation> annotation,
            @NonNull Throwable cause)
    {
        return new IncompatibleReturnProcessorException(annotation, cause);
    }
}
