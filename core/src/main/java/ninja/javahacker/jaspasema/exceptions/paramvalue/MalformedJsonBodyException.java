package ninja.javahacker.jaspasema.exceptions.paramvalue;

import java.lang.reflect.Parameter;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class MalformedJsonBodyException extends ParameterValueException {
    private static final long serialVersionUID = 1L;

    public MalformedJsonBodyException(
            /*@NonNull*/ Parameter parameter,
            /*@NonNull*/ Throwable cause)
    {
        super(parameter, cause);
    }
}
