package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class EmptyDateFormatException extends BadServiceMappingException {

    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_TEMPLATE = "Empty date format at @$A$ annotation.";

    @NonNull
    private final Class<? extends Annotation> annotation;

    protected EmptyDateFormatException(
            /*@NonNull*/ Method method,
            /*@NonNull*/ Class<? extends Annotation> annotation)
    {
        super(method, MESSAGE_TEMPLATE.replace("$A$", annotation.getSimpleName()));
        this.annotation = annotation;
    }

    protected EmptyDateFormatException(
            /*@NonNull*/ Parameter parameter,
            /*@NonNull*/ Class<? extends Annotation> annotation)
    {
        super(parameter, MESSAGE_TEMPLATE.replace("$A$", annotation.getSimpleName()));
        this.annotation = annotation;
    }

    public static EmptyDateFormatException create(
            @NonNull Method method,
            @NonNull Class<? extends Annotation> annotation)
    {
        return new EmptyDateFormatException(method, annotation);
    }

    public static EmptyDateFormatException create(
            @NonNull Parameter parameter,
            @NonNull Class<? extends Annotation> annotation)
    {
        return new EmptyDateFormatException(parameter, annotation);
    }
}
