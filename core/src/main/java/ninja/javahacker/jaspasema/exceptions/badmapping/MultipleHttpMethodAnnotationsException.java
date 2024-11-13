package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Method;

/**
 * Thrown when multiple HTTP-methods annotations are present in the same method, and only one should.
 * @author Victor Williams Stafusa da Silva
 */
public class MultipleHttpMethodAnnotationsException extends BadServiceMappingException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance specifying which is the offending method.
     * @param method The offending method.
     * @throws IllegalArgumentException If {@code method} is {@code null}.
     */
    public MultipleHttpMethodAnnotationsException(/*@NonNull*/ Method method) {
        super(method);
    }
}
