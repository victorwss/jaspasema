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

    @NonNull
    private final Class<? extends Annotation> annotation;

    public EmptyDateFormatException(
            /*@NonNull*/ Method method,
            @NonNull Class<? extends Annotation> annotation)
    {
        super(method);
        this.annotation = annotation;
    }

    public EmptyDateFormatException(
            /*@NonNull*/ Parameter parameter,
            @NonNull Class<? extends Annotation> annotation)
    {
        super(parameter);
        this.annotation = annotation;
    }

    @TemplateField("A")
    public String getAnnotationName() {
        return annotation.getSimpleName();
    }
}
