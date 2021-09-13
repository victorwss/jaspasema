package ninja.javahacker.jaspasema.exceptions.paramvalue;

import java.lang.reflect.Parameter;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class AbsentRequiredParameterException extends ParameterValueException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance specifying which is the offending parameter.
     * @param parameter The offending parameter.
     * @throws IllegalArgumentException If {@code parameter} is {@code null}.
     */
    public AbsentRequiredParameterException(/*@NonNull*/ Parameter parameter) {
        super(parameter);
    }
}
