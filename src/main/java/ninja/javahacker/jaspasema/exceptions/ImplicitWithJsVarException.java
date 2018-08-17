package ninja.javahacker.jaspasema.exceptions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class ImplicitWithJsVarException extends BadServiceMappingException {
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_TEMPLATE = "The @$A$ annotation shouldn't have jsVar not empty and be implicit.";

    @NonNull
    private final Class<? extends Annotation> annotation;

    protected ImplicitWithJsVarException(
            /*@NonNull*/ Parameter parameter,
            /*@NonNull*/ Class<? extends Annotation> annotation)
    {
        super(parameter, MESSAGE_TEMPLATE.replace("$A$", annotation.getSimpleName()));
        this.annotation = annotation;
    }

    public static ImplicitWithJsVarException create(
            @NonNull Parameter parameter,
            @NonNull Class<? extends Annotation> annotation)
    {
        return new ImplicitWithJsVarException(parameter, annotation);
    }
}
