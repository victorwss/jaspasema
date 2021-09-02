package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Method;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.processor.ResultSerializer;

/**
 * Thrown when two or more {@link ResultSerializer}-annotated annotations are defined on the same method for some exception type.
 * @author Victor Williams Stafusa da Silva
 */
public class ConflictingAnnotationsThrowsException extends BadServiceMappingException {

    private static final long serialVersionUID = 1L;

    /**
     * The exception type with two or more {@link ResultSerializer}-annotated annotations.
     * -- GETTER --
     * Retrieves whic was the exception type with two or more {@link ResultSerializer}-annotated annotations.
     * @return The exception type with two or more {@link ResultSerializer}-annotated annotations.
     */
    @Getter
    @NonNull
    private final Class<? extends Throwable> exceptionType;

    /**
     * Creates an instance specifying which is the offending class.
     * @param targetClass The offending class.
     * @param exceptionType The exception type with two or more {@link ResultSerializer}-annotated annotations.
     * @throws IllegalArgumentException If {@code targetClass} or {@code exceptionType} are {@code null}.
     */
    public ConflictingAnnotationsThrowsException(
            /*@NonNull*/ Class<?> targetClass,
            @NonNull Class<? extends Throwable> exceptionType)
    {
        super(targetClass);
        this.exceptionType = exceptionType;
    }

    /**
     * Creates an instance specifying which is the offending method.
     * @param method The offending method.
     * @param exceptionType The exception type with two or more {@link ResultSerializer}-annotated annotations.
     * @throws IllegalArgumentException If {@code method} or {@code exceptionType} are {@code null}.
     */
    public ConflictingAnnotationsThrowsException(
            /*@NonNull*/ Method method,
            @NonNull Class<? extends Throwable> exceptionType)
    {
        super(method);
        this.exceptionType = exceptionType;
    }

    @NonNull
    @TemplateField("X")
    public String getExceptionTypeName() {
        return exceptionType.getSimpleName();
    }
}
