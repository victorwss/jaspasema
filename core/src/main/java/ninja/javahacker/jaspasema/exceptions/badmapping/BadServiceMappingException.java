package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import ninja.javahacker.jaspasema.exceptions.JaspasemaException;

/**
 * @author Victor Williams Stafusa da Silva
 */
public abstract class BadServiceMappingException extends JaspasemaException {
    private static final long serialVersionUID = 1L;

    protected BadServiceMappingException(/*@NonNull*/ Parameter parameter) {
        super(parameter);
    }

    protected BadServiceMappingException(/*@NonNull*/ Parameter parameter, /*@NonNull*/ Throwable cause) {
        super(parameter, cause);
    }

    protected BadServiceMappingException(/*@NonNull*/ Method method) {
        super(method);
    }

    protected BadServiceMappingException(/*@NonNull*/ Method method, /*@NonNull*/ Throwable cause) {
        super(method, cause);
    }

    protected BadServiceMappingException(/*@NonNull*/ Class<?> declaringClass) {
        super(declaringClass);
    }

    protected BadServiceMappingException(/*@NonNull*/ Class<?> declaringClass, /*@NonNull*/ Throwable cause) {
        super(declaringClass, cause);
    }
}
