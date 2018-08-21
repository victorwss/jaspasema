package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class VoidWithValueReturnTypeException extends BadServiceMappingException {
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_TEMPLATE = "Methods returning void should not feature @$A$-annotated annotations.";

    @NonNull
    private final Class<? extends Annotation> annotation;

    protected VoidWithValueReturnTypeException(/*@NonNull*/ Method method, /*@NonNull*/ Class<? extends Annotation> annotation) {
        super(method, MESSAGE_TEMPLATE.replace("$A$", annotation.getSimpleName()));
        this.annotation = annotation;
    }

    public static VoidWithValueReturnTypeException create(@NonNull Method method, @NonNull Class<? extends Annotation> annotation) {
        return new VoidWithValueReturnTypeException(method, annotation);
    }
}
