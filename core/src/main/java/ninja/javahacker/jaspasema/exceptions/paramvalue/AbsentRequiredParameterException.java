package ninja.javahacker.jaspasema.exceptions.paramvalue;

import java.lang.reflect.Parameter;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class AbsentRequiredParameterException extends ParameterValueException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance specifying whose is the problematic parameter.
     * @param parameter The problematic parameter.
     */
    public AbsentRequiredParameterException(/*@NonNull*/ Parameter parameter) {
        super(parameter);
    }
}
