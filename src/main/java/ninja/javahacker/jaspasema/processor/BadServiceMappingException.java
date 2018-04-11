package ninja.javahacker.jaspasema.processor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class BadServiceMappingException extends Exception {
    private static final long serialVersionUID = 1L;

    @NonNull
    @Getter
    private final Optional<Parameter> parameter;

    @NonNull
    @Getter
    private final Method method;

    public BadServiceMappingException(/*@NonNull*/ Parameter parameter, /*@NonNull*/ String message) {
        this(parameter, message, null);
    }

    public BadServiceMappingException(/*@NonNull*/ Parameter parameter, /*@NonNull*/ String message, /*@NonNull*/ Throwable cause) {
        super("[" + parameter + "] " + message, cause);
        this.parameter = Optional.of(parameter);
        this.method = (Method) parameter.getDeclaringExecutable();
    }

    public BadServiceMappingException(/*@NonNull*/ Method method, /*@NonNull*/ String message) {
        this(method, message, null);
    }

    public BadServiceMappingException(/*@NonNull*/ Method method, /*@NonNull*/ String message, /*@NonNull*/ Throwable cause) {
        super("[" + method + "] " + message, cause);
        this.parameter = Optional.empty();
        this.method = method;
    }
}
