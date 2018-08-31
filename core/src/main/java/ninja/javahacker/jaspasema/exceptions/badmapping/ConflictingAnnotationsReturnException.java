package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Method;

/**
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
