package ninja.javahacker.jaspasema.exceptions.paramvalue;

import java.lang.reflect.Parameter;
import ninja.javahacker.jaspasema.exceptions.JaspasemaException;

/**
 * @author Victor Williams Stafusa da Silva
 */
public abstract class ParameterValueException extends JaspasemaException {
    private static final long serialVersionUID = 1L;

    protected ParameterValueException(/*@NonNull*/ Parameter parameter) {
        super(parameter);
    }

    protected ParameterValueException(/*@NonNull*/ Parameter parameter, /*@NonNull*/ Throwable cause) {
        super(parameter, cause);
    }
}
