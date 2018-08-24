package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class DontKnowHowToHandleAnnotationException extends BadServiceMappingException {
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_TEMPLATE = "Don't know how to handle @$A$.";

    @NonNull
    private final Class<? extends Annotation> annotation;

    protected DontKnowHowToHandleAnnotationException(
            /*@NonNull*/ Method method,
            /*@NonNull*/ Class<? extends Annotation> annotation)
    {
        super(method, MESSAGE_TEMPLATE.replace("$A$", annotation.getSimpleName()));
        this.annotation = annotation;
    }

    public static DontKnowHowToHandleAnnotationException create(
            @NonNull Method method,
            @NonNull Class<? extends Annotation> annotation)
    {
        return new DontKnowHowToHandleAnnotationException(method, annotation);
    }
}
