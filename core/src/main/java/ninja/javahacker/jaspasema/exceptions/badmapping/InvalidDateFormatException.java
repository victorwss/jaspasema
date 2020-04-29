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

    @NonNull
    private final Class<? extends Annotation> annotation;

    @NonNull
    private final String format;

    public InvalidDateFormatException(
            /*@NonNull*/ Method method,
            @NonNull Class<? extends Annotation> annotation,
            @NonNull String format)
    {
        super(method);
        this.annotation = annotation;
        this.format = format;
    }

    public InvalidDateFormatException(
            /*@NonNull*/ Parameter parameter,
            @NonNull Class<? extends Annotation> annotation,
            @NonNull String format)
    {
        super(parameter);
        this.annotation = annotation;
        this.format = format;
    }

    @NonNull
    @TemplateField("A")
    public String getAnnotationName() {
        return annotation.getSimpleName();
    }

    @NonNull
    @TemplateField("F")
    public String getFormat() {
        return format;
    }
}
