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

    @NonNull
    private final Class<? extends Annotation> annotation;

    public DontKnowHowToHandleAnnotationException(
            /*@NonNull*/ Method method,
            @NonNull Class<? extends Annotation> annotation)
    {
        super(method);
        this.annotation = annotation;
    }

    @NonNull
    @TemplateField("A")
    public String getAnnotationName() {
        return annotation.getSimpleName();
    }
}
