package ninja.javahacker.jaspasema.processor;

import java.lang.reflect.Method;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class MalformedReturnValueException extends Exception {
    private static final long serialVersionUID = 1L;

    @Getter
    @NonNull
    private final Object returnedValue;

    @Getter
    @NonNull
    private final Method method;

    public MalformedReturnValueException(
            /*@NonNull*/ Object returnedValue,
            /*@NonNull*/ Method method,
            /*@NonNull*/ String message,
            /*@NonNull*/ Throwable cause)
    {
        super("[" + method + "] " + message, cause);
        this.returnedValue = returnedValue;
        this.method = method;
    }
}
