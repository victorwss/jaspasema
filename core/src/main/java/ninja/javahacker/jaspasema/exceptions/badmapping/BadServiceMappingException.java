package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import ninja.javahacker.jaspasema.exceptions.JaspasemaException;

/**
 * Superclass of all exceptions that denote incorrect usage of Jaspasema's annotations.
 * @author Victor Williams Stafusa da Silva
 */
public abstract class BadServiceMappingException extends JaspasemaException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs an instance specifying a method parameter as the cause of this exception.
     * @param parameter The method parameter that is related to this exception.
     * @throws IllegalArgumentException If {@code parameter} is {@code null}.
     */
    protected BadServiceMappingException(/*@NonNull*/ Parameter parameter) {
        super(parameter);
    }

    /**
     * Constructs an instance specifying both a method parameter and another exception as the cause of this exception.
     * @param parameter The method parameter that is related to this exception.
     * @param cause Another exception that is the cause of this exception.
     * @throws IllegalArgumentException If any of {@code parameter} or {@code cause} are {@code null}.
     */
    protected BadServiceMappingException(/*@NonNull*/ Parameter parameter, /*@NonNull*/ Throwable cause) {
        super(parameter, cause);
    }

    /**
     * Constructs an instance specifying a method as the cause of this exception.
     * @param method The method that is related to this exception.
     * @throws IllegalArgumentException If {@code method} is {@code null}.
     */
    protected BadServiceMappingException(/*@NonNull*/ Method method) {
        super(method);
    }

    /**
     * Constructs an instance specifying both a method and another exception as the cause of this exception.
     * @param method The method that is related to this exception.
     * @param cause Another exception that is the cause of this exception.
     * @throws IllegalArgumentException If any of {@code method} or {@code cause} are {@code null}.
     */
    protected BadServiceMappingException(/*@NonNull*/ Method method, /*@NonNull*/ Throwable cause) {
        super(method, cause);
    }

    /**
     * Constructs an instance specifying a class as the cause of this exception.
     * @param declaringClass The class that is related to this exception.
     * @throws IllegalArgumentException If {@code declaringClass} is {@code null}.
     */
    protected BadServiceMappingException(/*@NonNull*/ Class<?> declaringClass) {
        super(declaringClass);
    }

    /**
     * Constructs an instance specifying both a class and another exception as the cause of this exception.
     * @param declaringClass The class that is related to this exception.
     * @param cause Another exception that is the cause of this exception.
     * @throws IllegalArgumentException If any of {@code declaringClass} or {@code cause} are {@code null}.
     */
    protected BadServiceMappingException(/*@NonNull*/ Class<?> declaringClass, /*@NonNull*/ Throwable cause) {
        super(declaringClass, cause);
    }
}
