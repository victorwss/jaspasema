package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Method;

/**
 * Thrown when some HTTP-method annotation should be present on a method, but isn't.
 * @author Victor Williams Stafusa da Silva
 */
public class NoHttpMethodAnnotationsException extends BadServiceMappingException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance specifying which is the offending method.
     * @param method The offending method.
     * @throws IllegalArgumentException If {@code method} is {@code null}.
     */
    public NoHttpMethodAnnotationsException(/*@NonNull*/ Method method) {
        super(method);
    }
}
