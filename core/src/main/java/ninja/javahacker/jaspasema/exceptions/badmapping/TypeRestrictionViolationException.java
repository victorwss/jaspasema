package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.ExceptionTemplate;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class TypeRestrictionViolationException extends BadServiceMappingException {
    private static final long serialVersionUID = 1L;

    @AllArgsConstructor
    public static enum AllowedTypes {
        SIMPLE, SIMPLE_LIST, SIMPLE_AND_LIST, DATE_TIME, DATE_TIME_LIST, LIST, HTTP;

        @Override
        public String toString() {
            return ExceptionTemplate.getExceptionTemplate().getAlloweds().get(this);
        }
    }

    @NonNull
    private final Class<? extends Annotation> annotation;

    @NonNull
    private final ReifiedGeneric<?> target;

    @NonNull
    private final AllowedTypes allowed;

    public TypeRestrictionViolationException(
            /*@NonNull*/ Method method,
            @NonNull Class<? extends Annotation> annotation,
            @NonNull AllowedTypes allowed,
            @NonNull ReifiedGeneric<?> target)
    {
        super(method);
        this.annotation = annotation;
        this.target = target;
        this.allowed = allowed;
    }

    public TypeRestrictionViolationException(
            /*@NonNull*/ Parameter parameter,
            @NonNull Class<? extends Annotation> annotation,
            @NonNull AllowedTypes allowed,
            @NonNull ReifiedGeneric<?> target)
    {
        super(parameter);
        this.annotation = annotation;
        this.target = target;
        this.allowed = allowed;
    }

    @TemplateField("A")
    public String getAnnotationName() {
        return annotation.getSimpleName();
    }

    @TemplateField("T")
    public String getTargetName() {
        return target.toString();
    }

    @TemplateField("V")
    public String getAllowedName() {
        return allowed.toString();
    }

    @TemplateField("U")
    private String getApplyType() {
        return getMethod().map(m -> ExceptionTemplate.getExceptionTemplate().getRm())
                .or(() -> getParameter().map(p -> ExceptionTemplate.getExceptionTemplate().getP()))
                .orElseThrow(AssertionError::new);
    }
}
