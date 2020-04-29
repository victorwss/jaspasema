package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.messages.ExceptionTemplate;
import ninja.javahacker.jaspasema.processor.AnnotatedParameter;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class TypeRestrictionViolationException extends BadServiceMappingException {
    private static final long serialVersionUID = 1L;

    public static enum AllowedTypes {
        SIMPLE, SIMPLE_LIST, SIMPLE_AND_LIST, DATE_TIME, DATE_TIME_LIST, LIST, HTTP;

        @NonNull
        @Override
        public String toString() {
            return ExceptionTemplate.getExceptionTemplate().nameFor(this);
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
            /*@NonNull*/ AnnotatedParameter<?, ?> param,
            @NonNull AllowedTypes allowed)
    {
        super(param.getParameter());
        this.annotation = param.getAnnotationType();
        this.target = param.getTarget();
        this.allowed = allowed;
    }

    @NonNull
    @TemplateField("A")
    public String getAnnotationName() {
        return annotation.getSimpleName();
    }

    @NonNull
    @TemplateField("T")
    public String getTargetName() {
        return target.toString();
    }

    @NonNull
    @TemplateField("V")
    public String getAllowedName() {
        return allowed.toString();
    }

    @NonNull
    @TemplateField("U")
    public String getApplyType() {
        return getMethod()
                .map(m -> ExceptionTemplate.getExceptionTemplate().getReturningMethods())
                .or(() -> getParameter().map(p -> ExceptionTemplate.getExceptionTemplate().getParameters()))
                .orElseThrow(AssertionError::new);
    }

    public static Supplier<TypeRestrictionViolationException> getFor(
            @NonNull AnnotatedParameter<?, ?> param,
            @NonNull AllowedTypes allowed)
    {
        return () -> new TypeRestrictionViolationException(param, allowed);
    }
}
