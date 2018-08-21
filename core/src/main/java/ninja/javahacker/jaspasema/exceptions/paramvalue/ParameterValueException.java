package ninja.javahacker.jaspasema.exceptions.paramvalue;

import java.lang.reflect.Parameter;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public abstract class ParameterValueException extends Exception {
    private static final long serialVersionUID = 1L;

    @NonNull
    private final Parameter parameter;

    protected ParameterValueException(/*@NonNull*/ Parameter parameter, /*@NonNull*/ String message) {
        this(parameter, message, null);
    }

    protected ParameterValueException(/*@NonNull*/ Parameter parameter, /*@NonNull*/ String message, /*@NonNull*/ Throwable cause) {
        super("[" + parameter + "|" + parameter.getDeclaringExecutable() + "] " + message, cause);
        this.parameter = parameter;
    }
}
