package ninja.javahacker.jaspasema.exceptions.retvalue;

import java.lang.reflect.Method;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.JaspasemaException;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public abstract class MalformedReturnValueException extends JaspasemaException {
    private static final long serialVersionUID = 1L;

    @NonNull
    private final Object returnedValue;

    protected MalformedReturnValueException(
            /*@NonNull*/ Method method,
            @NonNull Object returnedValue,
            /*@NonNull*/ Throwable cause)
    {
        super(method, cause);
        this.returnedValue = returnedValue;
    }
}
