package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * Thrown when the return type of a method or the type of some parameter is incompatible with the requirements of some
 * of its annotations.
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class ReturnTypeRestrictionViolationException extends BadServiceMappingException {

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
     * Creates an instance specifying a offending method.
     * @param method The offending method.
     * @param annotation The offending annotation class.
     * @param allowed The types that are allowed for the offending annotation class.
     * @param target The return type of the offending method.
     * @throws IllegalArgumentException If {@code method}, {@code annotation}, {@code allowed} or {@code target} are {@code null}.
     */
    public ReturnTypeRestrictionViolationException(
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
}
