package ninja.javahacker.jaspasema.processor;

import java.lang.reflect.Parameter;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class MalformedParameterException extends Exception {
    private static final long serialVersionUID = 1L;

    @NonNull
    private final Parameter parameter;

    public MalformedParameterException(/*@NonNull*/ Parameter parameter, /*@NonNull*/ String message) {
        this(parameter, message, null);
    }

    public MalformedParameterException(/*@NonNull*/ Parameter parameter, /*@NonNull*/ String message, /*@NonNull*/ Throwable cause) {
        super("[" + parameter + "] " + message, cause);
        this.parameter = parameter;
    }
}
