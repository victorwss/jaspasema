package ninja.javahacker.jaspasema.exceptions.retvalue;

import java.lang.reflect.Method;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class MalformedJsonReturnValueException extends MalformedReturnValueException {
    private static final long serialVersionUID = 1L;

    public MalformedJsonReturnValueException(
            /*@NonNull*/ Method method,
            /*@NonNull*/ Object returnedValue,
            /*@NonNull*/ Throwable cause)
    {
        super(method, returnedValue, cause);
    }
}
