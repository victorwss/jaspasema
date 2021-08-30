package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Method;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class MissingPathException extends BadServiceMappingException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance specifying which is the offending method.
     * @param method The offending method.
     */
    public MissingPathException(/*@NonNull*/ Method method) {
        super(method);
    }
}
