package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Method;
import ninja.javahacker.jaspasema.processor.ResultProcessor;

/**
 * Thrown when two or more {@link ResultProcessor}-annotated annotations are defined on the same parameter.
 * @author Victor Williams Stafusa da Silva
 */
public class ConflictingMappingOnReturnTypeException extends BadServiceMappingException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance specifying which is the offending method.
     * @param method The offending method.
     * @throws IllegalArgumentException If {@link method} is {@code null}.
     */
    public ConflictingMappingOnReturnTypeException(/*@NonNull*/ Method method) {
        super(method);
    }
}
