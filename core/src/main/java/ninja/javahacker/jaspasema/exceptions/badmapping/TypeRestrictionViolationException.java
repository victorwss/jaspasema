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
 * Thrown when the return type of a method or the type of some parameter is incompatible with the requirements of some
 * of its annotations.
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class TypeRestrictionViolationException extends BadServiceMappingException {
    private static final long serialVersionUID = 1L;

    /**
     * Enumerates what are possibly allowed for specific annotations.
     * @author Victor Williams Stafusa da Silva
     */
    public static enum AllowedTypes {
        /** Means that only primitives, primitive wrappers, Strings and date/time are allowed. */
        SIMPLE,

        /** Means that only lists of primitive wrappers, Strings and date/time are allowed. */
        SIMPLE_LIST,

        /** Means that only primitives, primitive wrappers, Strings, date/time and lists of those are allowed. */
        SIMPLE_AND_LIST,

        /** Means that only date/time is allowed. */
        DATE_TIME,

        /** Means that only lists of date/time are allowed. */
        DATE_TIME_LIST,

        /** Means that only lists are allowed. */
        LIST,

        /** Means that only Request, Response and Session are allowed. */
        HTTP;

        /**
         * Gives a localized {@link String} representation of {@code this}.
         * @return A localized {@link String} representation of {@code this}.
         */
        @NonNull
        @Override
        public String toString() {
            return ExceptionTemplate.getExceptionTemplate().nameFor(this);
        }
    }

    /**
     * The class of the offending annotation.
     * -- GETTER --
     * Gives the offending annotation class.
     * @return The offending annotation class.
     */
    @NonNull
    private final Class<? extends Annotation> annotation;

    /**
     * The return type of the offending method or type of the offending parameter.
     * -- GETTER --
     * Gives the return type of the offending method or type of the offending parameter.
     * @return The return type of the offending method or type of the offending parameter.
     */
    @NonNull
    private final ReifiedGeneric<?> target;

    /**
     * The types that are allowed for the offending annotation class.
     * -- GETTER --
     * Gives the types that are allowed for the offending annotation class.
     * @return The types that are allowed for the offending annotation class.
     */
    @NonNull
    private final AllowedTypes allowed;

    /**
     * Creates an instance specifying a offending method.
     * @param method The offending method.
     * @param annotation The offending annotation class.
     * @param allowed The types that are allowed for the offending annotation class.
     * @param target The return type of the offending method.
     */
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

    /**
     * Creates an instance specifying a offending parameter.
     * @param param The offending parameter and its annotations.
     * @param allowed The types that are allowed for the offending annotation class.
     */
    public TypeRestrictionViolationException(
            /*@NonNull*/ AnnotatedParameter<?, ?> param,
            @NonNull AllowedTypes allowed)
    {
        super(param.getParameter());
        this.annotation = param.getAnnotationType();
        this.target = param.getTarget();
        this.allowed = allowed;
    }

    /**
     * Gives the name of the offending annotation class.
     * @return The name of the offending annotation class.
     */
    @NonNull
    @TemplateField("A")
    public String getAnnotationName() {
        return annotation.getSimpleName();
    }

    /**
     * Gives the name of the return type of the offending method or type of the offending parameter.
     * @return The name of the return type of the offending method or type of the offending parameter.
     */
    @NonNull
    @TemplateField("T")
    public String getTargetName() {
        return target.toString();
    }

    /**
     * Gives the name of the types that are allowed for the offending annotation class.
     * @return The name of the types that are allowed for the offending annotation class.
     */
    @NonNull
    @TemplateField("V")
    public String getAllowedName() {
        return allowed.toString();
    }

    /**
     * Tells if the offending annotation pertains to a method return type or to a parameter type.
     * @return Either {@code "returning methods"} or {@code "parameters"}, possibly properly localized.
     */
    @NonNull
    @TemplateField("U")
    public String getApplyType() {
        return getMethod()
                .map(m -> ExceptionTemplate.getExceptionTemplate().getReturningMethods())
                .or(() -> getParameter().map(p -> ExceptionTemplate.getExceptionTemplate().getParameters()))
                .orElseThrow(AssertionError::new);
    }

    /**
     * Gives a supplier that creates an instance of {@code TypeRestrictionViolationException} by specifying a offending parameter.
     * @param param The offending parameter and its annotations.
     * @param allowed The types that are allowed for the offending annotation class.
     * @return A supllier that instantiates a {@code TypeRestrictionViolationException}.
     */
    public static Supplier<TypeRestrictionViolationException> getFor(
            @NonNull AnnotatedParameter<?, ?> param,
            @NonNull AllowedTypes allowed)
    {
        return () -> new TypeRestrictionViolationException(param, allowed);
    }
}
