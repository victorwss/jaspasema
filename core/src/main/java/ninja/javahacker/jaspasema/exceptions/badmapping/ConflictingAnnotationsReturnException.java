package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Method;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class ConflictingAnnotationsReturnException extends BadServiceMappingException {
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE_TEMPLATE =
            "Conflicting @ReturnSerializer-annotated annotations on method for return type.";

    protected ConflictingAnnotationsReturnException(/*@NonNull*/ Class<?> targetClass) {
        super(targetClass, MESSAGE_TEMPLATE);
    }

    protected ConflictingAnnotationsReturnException(/*@NonNull*/ Method method) {
        super(method, MESSAGE_TEMPLATE);
    }

    public static ConflictingAnnotationsReturnException create(@NonNull Class<?> targetClass) {
        return new ConflictingAnnotationsReturnException(targetClass);
    }

    public static ConflictingAnnotationsReturnException create(@NonNull Method method) {
        return new ConflictingAnnotationsReturnException(method);
    }
}
