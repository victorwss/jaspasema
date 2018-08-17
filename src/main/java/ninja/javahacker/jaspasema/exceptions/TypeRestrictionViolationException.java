package ninja.javahacker.jaspasema.exceptions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class TypeRestrictionViolationException extends BadServiceMappingException {
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_TEMPLATE =
            "The @$A$ annotation must be used only on $V$ $U$. The found type was $T$.";

    @AllArgsConstructor
    public static enum AllowedTypes {
        SIMPLE("primitives, primitive wrappers, Strings and date/time"),
        SIMPLE_LIST("lists of primitive wrappers, Strings and date/time"),
        SIMPLE_AND_LIST("primitives, primitive wrappers, Strings, date/time and lists of those"),
        DATE_TIME("date/time"),
        DATE_TIME_LIST("lists of date/time"),
        LIST("list"),
        HTTP("Request, Response or Session");

        private final String text;

        @Override
        public String toString() {
            return text;
        }
    }

    @NonNull
    private final Class<? extends Annotation> annotation;

    @NonNull
    private final ReifiedGeneric<?> target;

    @NonNull
    private final AllowedTypes allowed;

    private static String template(
            /*@NonNull*/ Class<? extends Annotation> annotation,
            /*@NonNull*/ AllowedTypes allowed,
            /*@NonNull*/ ReifiedGeneric<?> target,
            /*@NonNull*/ String type)
    {
        return MESSAGE_TEMPLATE
                .replace("$A$", annotation.getSimpleName())
                .replace("$T$", target.toString())
                .replace("$V$", allowed.toString())
                .replace("$U$", type);
    }

    protected TypeRestrictionViolationException(
            /*@NonNull*/ Method method,
            /*@NonNull*/ Class<? extends Annotation> annotation,
            /*@NonNull*/ AllowedTypes allowed,
            /*@NonNull*/ ReifiedGeneric<?> target)
    {
        super(method, template(annotation, allowed, target, "returning methods"));
        this.annotation = annotation;
        this.target = target;
        this.allowed = allowed;
    }

    protected TypeRestrictionViolationException(
            /*@NonNull*/ Parameter parameter,
            /*@NonNull*/ Class<? extends Annotation> annotation,
            /*@NonNull*/ AllowedTypes allowed,
            /*@NonNull*/ ReifiedGeneric<?> target)
    {
        super(parameter, template(annotation, allowed, target, "parameters"));
        this.annotation = annotation;
        this.target = target;
        this.allowed = allowed;
    }

    public static TypeRestrictionViolationException create(
            @NonNull Method method,
            @NonNull Class<? extends Annotation> annotation,
            @NonNull AllowedTypes allowed,
            @NonNull ReifiedGeneric<?> target)
    {
        return new TypeRestrictionViolationException(method, annotation, allowed, target);
    }

    public static TypeRestrictionViolationException create(
            @NonNull Parameter parameter,
            @NonNull Class<? extends Annotation> annotation,
            @NonNull AllowedTypes allowed,
            @NonNull ReifiedGeneric<?> target)
    {
        return new TypeRestrictionViolationException(parameter, annotation, allowed, target);
    }
}
