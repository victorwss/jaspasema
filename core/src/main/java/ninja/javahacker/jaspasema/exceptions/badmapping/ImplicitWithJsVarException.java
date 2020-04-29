package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.annotation.Annotation;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.processor.AnnotatedParameter;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class ImplicitWithJsVarException extends BadServiceMappingException {
    private static final long serialVersionUID = 1L;

    @NonNull
    private final Class<? extends Annotation> annotation;

    public ImplicitWithJsVarException(/*@NonNull*/ AnnotatedParameter<? extends Annotation, ?> param) {
        super(param.getParameter());
        this.annotation = param.getAnnotationType();
    }

    @NonNull
    @TemplateField("A")
    public String getAnnotationName() {
        return annotation.getSimpleName();
    }
}
