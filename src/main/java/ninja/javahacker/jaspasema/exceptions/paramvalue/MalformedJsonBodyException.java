package ninja.javahacker.jaspasema.exceptions.paramvalue;

import java.lang.reflect.Parameter;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class MalformedJsonBodyException extends ParameterValueException {
    private static final long serialVersionUID = 1L;

    public static final String TEMPLATE = "The body request data failed to be parseable as JSON.";

    protected MalformedJsonBodyException(
            /*@NonNull*/ Parameter parameter,
            /*@NonNull*/ Throwable cause)
    {
        super(parameter, TEMPLATE, cause);
    }

    public static MalformedJsonBodyException create(
            @NonNull Parameter parameter,
            @NonNull Throwable cause)
    {
        return new MalformedJsonBodyException(parameter, cause);
    }
}
