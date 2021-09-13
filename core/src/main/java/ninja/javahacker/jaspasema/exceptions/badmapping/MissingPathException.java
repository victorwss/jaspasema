package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Method;
import ninja.javahacker.jaspasema.Path;

/**
 * Thrown when some method of an interface used for routing lacks the {@link Path} annotation.
 * @author Victor Williams Stafusa da Silva
 */
public class MissingPathException extends BadServiceMappingException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance specifying which is the offending method.
     * @param method The offending method.
     * @throws IllegalArgumentException If {@code method} is {@code null}.
     */
    public MissingPathException(/*@NonNull*/ Method method) {
        super(method);
    }
}
