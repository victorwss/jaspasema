package ninja.javahacker.jaspasema.exceptions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import lombok.Getter;
import lombok.NonNull;

/**
 * Superclass of all exceptions that denote that a Jaspasema annotation processor is broken.
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public abstract class MalformedProcessorException extends JaspasemaException {
    private static final long serialVersionUID = 1L;

    /**
     * The annotation class that was malconfigued.
     * -- GETTER --
     * Retrieves the annotation class that was malconfigued.
     * @return The annotation class that was malconfigued.
     */
    @NonNull
    private final Class<? extends Annotation> badAnnotation;

    /**
     * The processor class that was malformed.
     * -- GETTER --
     * Retrieves the processor class that was malformed.
     * @return The processor class that was malformed.
     */
    @NonNull
    private final Class<?> processorClass;

    /**
     * Constructs an instance specifying both a method parameter and another exception as the cause of this exception.
     * @param parameter The method parameter that is related to this exception.
     * @param badAnnotation Which annotation class was malconfigued.
     * @param processorClass Which processor class was malformed.
     * @param cause Another exception that is the cause of this exception.
     * @throws IllegalArgumentException If any of {@code parameter}, {@code badAnnotation}, {@code processorClass} or
     *     {@code cause} are {@code null}.
     */
    protected MalformedProcessorException(
            /*@NonNull*/ Parameter parameter,
            @NonNull Class<? extends Annotation> badAnnotation,
            @NonNull Class<?> processorClass,
            /*@NonNull*/ Throwable cause)
    {
        super(parameter, cause);
        this.badAnnotation = badAnnotation;
        this.processorClass = processorClass;
    }

    /**
     * Constructs an instance specifying a method parameter as the cause of this exception.
     * @param parameter The method parameter that is related to this exception.
     * @param badAnnotation Which annotation class was malconfigued.
     * @param processorClass Which processor class was malformed.
     * @throws IllegalArgumentException If any of {@code parameter}, {@code badAnnotation}, {@code processorClass} are
     *     {@code null}.
     */
    protected MalformedProcessorException(
            /*@NonNull*/ Parameter parameter,
            @NonNull Class<? extends Annotation> badAnnotation,
            @NonNull Class<?> processorClass)
    {
        super(parameter);
        this.badAnnotation = badAnnotation;
        this.processorClass = processorClass;
    }

    /**
     * Constructs an instance specifying both a method and another exception as the cause of this exception.
     * @param method The method that is related to this exception.
     * @param badAnnotation Which annotation class was malconfigued.
     * @param processorClass Which processor class was malformed.
     * @param cause Another exception that is the cause of this exception.
     * @throws IllegalArgumentException If any of {@code method}, {@code badAnnotation}, {@code processorClass} or
     *     {@code cause} are {@code null}.
     */
    protected MalformedProcessorException(
            /*@NonNull*/ Method method,
            @NonNull Class<? extends Annotation> badAnnotation,
            @NonNull Class<?> processorClass,
            /*@NonNull*/ Throwable cause)
    {
        super(method, cause);
        this.badAnnotation = badAnnotation;
        this.processorClass = processorClass;
    }

    /**
     * Constructs an instance specifying a method as the cause of this exception.
     * @param method The method that is related to this exception.
     * @param badAnnotation Which annotation class was malconfigued.
     * @param processorClass Which processor class was malformed.
     * @throws IllegalArgumentException If any of {@code method}, {@code badAnnotation} or {@code processorClass} are
     *     {@code null}.
     */
    protected MalformedProcessorException(
            /*@NonNull*/ Method method,
            @NonNull Class<? extends Annotation> badAnnotation,
            @NonNull Class<?> processorClass)
    {
        super(method);
        this.badAnnotation = badAnnotation;
        this.processorClass = processorClass;
    }

    /**
     * Constructs an instance specifying both a declaring class and another exception as the cause of this exception.
     * @param declaringClass The declaring class that is related to this exception.
     * @param badAnnotation Which annotation class was malconfigued.
     * @param processorClass Which processor class was malformed.
     * @param cause Another exception that is the cause of this exception.
     * @throws IllegalArgumentException If any of {@code declaringClass}, {@code badAnnotation}, {@code processorClass}
     *     or {@code cause} are {@code null}.
     */
    protected MalformedProcessorException(
            /*@NonNull*/ Class<?> declaringClass,
            @NonNull Class<? extends Annotation> badAnnotation,
            @NonNull Class<?> processorClass,
            /*@NonNull*/ Throwable cause)
    {
        super(declaringClass, cause);
        this.badAnnotation = badAnnotation;
        this.processorClass = processorClass;
    }

    /**
     * Constructs an instance specifying a declaring class as the cause of this exception.
     * @param declaringClass The declaring class that is related to this exception.
     * @param badAnnotation Which annotation class was malconfigued.
     * @param processorClass Which processor class was malformed.
     * @throws IllegalArgumentException If any of {@code declaringClass}, {@code badAnnotation} or
     *     {@code processorClass} are {@code null}.
     */
    protected MalformedProcessorException(
            /*@NonNull*/ Class<?> declaringClass,
            @NonNull Class<? extends Annotation> badAnnotation,
            @NonNull Class<?> processorClass)
    {
        super(declaringClass);
        this.badAnnotation = badAnnotation;
        this.processorClass = processorClass;
    }

    /**
     * Retrieves the name of the annotation class that was malconfigued.
     * @return The name of the annotation class that was malconfigued.
     */
    @NonNull
    @TemplateField("A")
    public String getAnnotationName() {
        return getBadAnnotation().getSimpleName();
    }

    @NonNull
    @TemplateField("R")
    public String getRemapperName() {
        return processorClass.getSimpleName();
    }
}
