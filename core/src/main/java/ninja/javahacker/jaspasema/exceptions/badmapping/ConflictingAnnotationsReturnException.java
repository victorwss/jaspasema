package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Method;
import ninja.javahacker.jaspasema.processor.ResultSerializer;

/**
 * Thrown when two or more {@link ResultSerializer}-annotated annotations are defined on the same method for the return type.
 * @author Victor Williams Stafusa da Silva
 */
public class ConflictingAnnotationsReturnException extends BadServiceMappingException {
    private static final long serialVersionUID = 1L;

    public ConflictingAnnotationsReturnException(/*@NonNull*/ Class<?> targetClass) {
        super(targetClass);
    }

    public ConflictingAnnotationsReturnException(/*@NonNull*/ Method method) {
        super(method);
    }
}
