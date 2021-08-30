package ninja.javahacker.jaspasema.exceptions.paramvalue;

import java.lang.reflect.Parameter;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class MalformedJsonBodyException extends ParameterValueException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance with a cause specifying which is the offending parameter.
     * @param parameter The offending parameter.
     * @param cause The cause.
     */
    public MalformedJsonBodyException(
            /*@NonNull*/ Parameter parameter,
            /*@NonNull*/ Throwable cause)
    {
        super(parameter, cause);
    }
}
