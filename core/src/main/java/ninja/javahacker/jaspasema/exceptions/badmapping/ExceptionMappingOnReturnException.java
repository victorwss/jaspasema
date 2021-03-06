package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Method;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class ExceptionMappingOnReturnException extends BadServiceMappingException {
    private static final long serialVersionUID = 1L;

    public ExceptionMappingOnReturnException(/*@NonNull*/ Method method) {
        super(method);
    }
}
