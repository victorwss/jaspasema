package ninja.javahacker.jaspasema.exceptions.paramvalue;

import java.lang.reflect.Parameter;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class AbsentRequiredParameterException extends ParameterValueException {
    private static final long serialVersionUID = 1L;

    public AbsentRequiredParameterException(/*@NonNull*/ Parameter parameter) {
        super(parameter);
    }
}
