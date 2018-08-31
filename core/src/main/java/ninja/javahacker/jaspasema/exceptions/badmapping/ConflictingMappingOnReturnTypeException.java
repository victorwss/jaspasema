package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Method;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class ConflictingMappingOnReturnTypeException extends BadServiceMappingException {
    private static final long serialVersionUID = 1L;

    public ConflictingMappingOnReturnTypeException(/*@NonNull*/ Method method) {
        super(method);
    }
}
