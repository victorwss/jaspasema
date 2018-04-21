package ninja.javahacker.jaspasema.processor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class BadServiceMappingException extends Exception {
    private static final long serialVersionUID = 1L;

    @NonNull
    private final Optional<Parameter> parameter;

    @NonNull
    private final Optional<Method> method;

    @NonNull
    private final Class<?> declaringClass;

    public BadServiceMappingException(/*@NonNull*/ Parameter parameter, /*@NonNull*/ String message) {
        this(parameter, message, null);
    }

    public BadServiceMappingException(/*@NonNull*/ Parameter parameter, /*@NonNull*/ String message, /*@NonNull*/ Throwable cause) {
        super("[" + parameter + "] " + message, cause);
        this.parameter = Optional.of(parameter);
        this.method = Optional.of((Method) parameter.getDeclaringExecutable());
        this.declaringClass = parameter.getDeclaringExecutable().getDeclaringClass();
    }

    public BadServiceMappingException(/*@NonNull*/ Method method, /*@NonNull*/ String message) {
        this(method, message, null);
    }

    public BadServiceMappingException(/*@NonNull*/ Method method, /*@NonNull*/ String message, /*@NonNull*/ Throwable cause) {
        super("[" + method + "] " + message, cause);
        this.parameter = Optional.empty();
        this.method = Optional.of(method);
        this.declaringClass = method.getDeclaringClass();
    }

    public BadServiceMappingException(/*@NonNull*/ Class<?> declaringClass, /*@NonNull*/ String message) {
        this(declaringClass, message, null);
    }

    public BadServiceMappingException(/*@NonNull*/ Class<?> declaringClass, /*@NonNull*/ String message, /*@NonNull*/ Throwable cause) {
        super("[" + declaringClass.getName() + "] " + message, cause);
        this.parameter = Optional.empty();
        this.method = Optional.empty();
        this.declaringClass = declaringClass;
    }
}
