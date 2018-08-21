package ninja.javahacker.jaspasema.exceptions.paramvalue;

import java.lang.reflect.Parameter;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class AbsentRequiredParameterException extends ParameterValueException {
    private static final long serialVersionUID = 1L;

    public static final String TEMPLATE = "The required parameter value was absent.";

    protected AbsentRequiredParameterException(/*@NonNull*/ Parameter parameter) {
        super(parameter, TEMPLATE);
    }

    public static AbsentRequiredParameterException create(@NonNull Parameter parameter) {
        return new AbsentRequiredParameterException(parameter);
    }
}
