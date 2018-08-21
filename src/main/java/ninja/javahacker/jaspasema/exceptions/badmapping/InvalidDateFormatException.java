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
public class InvalidDateFormatException extends BadServiceMappingException {

    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_TEMPLATE = "Invalid date format \"$F$\" at @$A$ annotation.";

    @NonNull
    private final Class<? extends Annotation> annotation;

    @NonNull
    private final String format;

    protected InvalidDateFormatException(
            /*@NonNull*/ Method method,
            /*@NonNull*/ Class<? extends Annotation> annotation,
            /*@NonNull*/ String format)
    {
        super(method, MESSAGE_TEMPLATE.replace("$F$", format).replace("$A$", annotation.getSimpleName()));
        this.annotation = annotation;
        this.format = format;
    }

    protected InvalidDateFormatException(
            /*@NonNull*/ Parameter parameter,
            /*@NonNull*/ Class<? extends Annotation> annotation,
            /*@NonNull*/ String format)
    {
        super(parameter, MESSAGE_TEMPLATE.replace("$F$", format).replace("$A$", annotation.getSimpleName()));
        this.annotation = annotation;
        this.format = format;
    }

    public static InvalidDateFormatException create(
            @NonNull Method method,
            @NonNull Class<? extends Annotation> annotation,
            @NonNull String format)
    {
        return new InvalidDateFormatException(method, annotation, format);
    }

    public static InvalidDateFormatException create(
            @NonNull Parameter parameter,
            @NonNull Class<? extends Annotation> annotation,
            @NonNull String format)
    {
        return new InvalidDateFormatException(parameter, annotation, format);
    }
}
