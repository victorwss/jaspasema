package ninja.javahacker.jaspasema.exceptions.retproc;

import java.lang.annotation.Annotation;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class ReturnProcessorConstructorException extends MalformedReturnProcessorException {
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_TEMPLATE =
            "Return processor constructor of class $R$ throwed an exception.";

    protected ReturnProcessorConstructorException(
            /*@NonNull*/ Class<? extends Annotation> annotation,
            /*@NonNull*/ Throwable cause)
    {
        super(annotation, MESSAGE_TEMPLATE.replace("$R$", annotation.getSimpleName()), cause);
    }

    public static ReturnProcessorConstructorException create(
            @NonNull Class<? extends Annotation> annotation,
            @NonNull Throwable cause)
    {
        return new ReturnProcessorConstructorException(annotation, cause);
    }
}
