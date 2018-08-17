package ninja.javahacker.jaspasema.exceptions;

import java.lang.reflect.Method;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class MalformedReturnValueException extends Exception {
    private static final long serialVersionUID = 1L;

    public static final String TEMPLATE = "Returned value couldn't be converted to JSON.";

    @NonNull
    private final Object returnedValue;

    @NonNull
    private final Method method;

    protected MalformedReturnValueException(
            /*@NonNull*/ Object returnedValue,
            /*@NonNull*/ Method method,
            /*@NonNull*/ String message,
            /*@NonNull*/ Throwable cause)
    {
        super("[" + method + "] " + message, cause);
        this.returnedValue = returnedValue;
        this.method = method;
    }

    public static MalformedReturnValueException create(
            @NonNull Object returnedValue,
            @NonNull Method method,
            @NonNull Throwable cause)
    {
        return new MalformedReturnValueException(returnedValue, method, TEMPLATE, cause);
    }
}
