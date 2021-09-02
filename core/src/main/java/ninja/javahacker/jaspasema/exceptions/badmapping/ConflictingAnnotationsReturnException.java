package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Method;
import ninja.javahacker.jaspasema.processor.ResultSerializer;

/**
 * Thrown when two or more {@link ResultSerializer}-annotated annotations are defined on the same method for the return type.
 * @author Victor Williams Stafusa da Silva
 */
public class ConflictingAnnotationsReturnException extends BadServiceMappingException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance specifying which is the offending class.
     * @param targetClass The offending class.
     * @throws IllegalArgumentException If {@code targetClass} is {@code null}.
     */
    public ConflictingAnnotationsReturnException(/*@NonNull*/ Class<?> targetClass) {
        super(targetClass);
    }

    /**
     * Creates an instance specifying which is the offending method.
     * @param method The offending method.
     * @throws IllegalArgumentException If {@code method} is {@code null}.
     */
    public ConflictingAnnotationsReturnException(/*@NonNull*/ Method method) {
        super(method);
    }
}
