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

    private static final String MESSAGE_TEMPLATE =
            "Conflicting @ReturnSerializer-annotated annotations on method for exception $X$.";

    @NonNull
    private final Class<? extends Throwable> type;

    protected ConflictingAnnotationsThrowsException(
            /*@NonNull*/ Class<?> targetClass,
            /*@NonNull*/ Class<? extends Throwable> type)
    {
        super(targetClass, MESSAGE_TEMPLATE.replace("$X$", type.getSimpleName()));
        this.type = type;
    }

    protected ConflictingAnnotationsThrowsException(
            /*@NonNull*/ Method method,
            /*@NonNull*/ Class<? extends Throwable> type)
    {
        super(method, MESSAGE_TEMPLATE.replace("$X$", type.getSimpleName()));
        this.type = type;
    }

    public static ConflictingAnnotationsThrowsException create(
            @NonNull Class<?> targetClass,
            @NonNull Class<? extends Throwable> type)
    {
        return new ConflictingAnnotationsThrowsException(targetClass, type);
    }

    public static ConflictingAnnotationsThrowsException create(
            @NonNull Method method,
            @NonNull Class<? extends Throwable> type)
    {
        return new ConflictingAnnotationsThrowsException(method, type);
    }
}
