package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Method;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class NoMappingOnReturnTypeException extends BadServiceMappingException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance specifying which is the offending method.
     * @param method The offending method.
     * @throws IllegalArgumentException If {@link method} is {@code null}.
     */
    public NoMappingOnReturnTypeException(/*@NonNull*/ Method method) {
        super(method);
    }
}
