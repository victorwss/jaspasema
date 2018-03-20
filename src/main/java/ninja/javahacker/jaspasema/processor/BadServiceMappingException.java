package ninja.javahacker.jaspasema.processor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class BadServiceMappingException extends Exception {
    private static final long serialVersionUID = 1L;

    public BadServiceMappingException(/*@NonNull*/ Parameter parameter, /*@NonNull*/ String message) {
        super("[" + parameter + "] " + message);
    }

    public BadServiceMappingException(Parameter parameter, String message, /*@NonNull*/ Throwable cause) {
        super("[" + parameter + "] " + message, cause);
    }

    public BadServiceMappingException(/*@NonNull*/ Method method, /*@NonNull*/ /*@NonNull*/ String message) {
        super("[" + method + "] " + message);
    }

    public BadServiceMappingException(/*@NonNull*/ Method method, /*@NonNull*/ String message, /*@NonNull*/ Throwable cause) {
        super("[" + method + "] " + message, cause);
    }
}
