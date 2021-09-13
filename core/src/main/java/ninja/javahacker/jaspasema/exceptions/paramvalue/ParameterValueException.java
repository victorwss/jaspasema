package ninja.javahacker.jaspasema.exceptions.paramvalue;

import java.lang.reflect.Parameter;
import ninja.javahacker.jaspasema.exceptions.JaspasemaException;

/**
 * @author Victor Williams Stafusa da Silva
 */
public abstract class ParameterValueException extends JaspasemaException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance specifying which is the offending parameter.
     * @param parameter The offending parameter.
     * @throws IllegalArgumentException If {@code parameter} is {@code null}.
     */
    protected ParameterValueException(/*@NonNull*/ Parameter parameter) {
        super(parameter);
    }

    /**
     * Creates an instance with a cause specifying which is the offending parameter.
     * @param parameter The offending parameter.
     * @param cause The cause.
     * @throws IllegalArgumentException If {@code parameter} or {@code cause} are {@code null}.
     */
    protected ParameterValueException(/*@NonNull*/ Parameter parameter, /*@NonNull*/ Throwable cause) {
        super(parameter, cause);
    }
}
