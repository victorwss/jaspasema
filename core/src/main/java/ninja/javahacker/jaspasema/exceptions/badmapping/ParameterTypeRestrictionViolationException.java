package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.processor.AnnotatedParameter;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * Thrown when the return type of a method or the type of some parameter is incompatible with the requirements of some
 * of its annotations.
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class ParameterTypeRestrictionViolationException extends BadServiceMappingException {

    private static final long serialVersionUID = 1L;

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
     * Creates an instance specifying a offending parameter.
     * @param param The offending parameter and its annotations.
     * @param allowed The types that are allowed for the offending annotation class.
     * @throws IllegalArgumentException If {@code param} or {@code allowed} are {@code null}.
     */
    public ParameterTypeRestrictionViolationException(
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
        return target.getType().getTypeName();
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
     * Gives a supplier that creates an instance of {@code TypeRestrictionViolationException} by specifying an offending parameter.
     * @param param The offending parameter and its annotations.
     * @param allowed The types that are allowed for the offending annotation class.
     * @return A supplier that instantiates a {@code TypeRestrictionViolationException}.
     * @throws IllegalArgumentException If {@code param} or {@code allowed} are {@code null}.
     */
    public static Supplier<ParameterTypeRestrictionViolationException> getFor(
            @NonNull AnnotatedParameter<?, ?> param,
            @NonNull AllowedTypes allowed)
    {
        return () -> new ParameterTypeRestrictionViolationException(param, allowed);
    }
}
