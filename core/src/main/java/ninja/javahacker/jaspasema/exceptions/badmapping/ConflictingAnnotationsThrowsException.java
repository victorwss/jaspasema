package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Method;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class ConflictingAnnotationsThrowsException extends BadServiceMappingException {
    private static final long serialVersionUID = 1L;

    @NonNull
    private final Class<? extends Throwable> exceptionType;

    public ConflictingAnnotationsThrowsException(
            /*@NonNull*/ Class<?> targetClass,
            @NonNull Class<? extends Throwable> exceptionType)
    {
        super(targetClass);
        this.exceptionType = exceptionType;
    }

    public ConflictingAnnotationsThrowsException(
            /*@NonNull*/ Method method,
            @NonNull Class<? extends Throwable> exceptionType)
    {
        super(method);
        this.exceptionType = exceptionType;
    }

    @TemplateField("X")
    public String getExceptionTypeName() {
        return exceptionType.getSimpleName();
    }
}
