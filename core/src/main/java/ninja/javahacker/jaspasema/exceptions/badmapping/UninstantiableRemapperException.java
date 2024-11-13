package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Method;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.ExceptionRemapper;

/**
 * Thrown when an attempt to construct an instance of some {@link ExceptionRemapper} fails because the
 * {@link ExceptionRemapper}'s class is unistantiable (i.e. it is an abstract class, an interface or has no public no-arg constructor).
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class UninstantiableRemapperException extends BadServiceMappingException {

    private static final long serialVersionUID = 1L;

    /**
     * The uninstantiable {@link ExceptionRemapper}'s class.
     * -- GETTER --
     * Tells which is the uninstantiable {@link ExceptionRemapper}'s class.
     * @return Which is the uninstantiable {@link ExceptionRemapper}'s class.
     */
    @NonNull
    private final Class<? extends ExceptionRemapper> remapper;

    /**
     * Creates an instance specifying which is the offending method.
     * @param method The offending method.
     * @param remapper The {@link ExceptionRemapper}'s class.
     * @param cause The exception raised by the attempt to instantiate the {@link ExceptionRemapper}'s class.
     * @throws IllegalArgumentException If {@code method}, {@code remapper} or {@code cause} are {@code null}.
     */
    public UninstantiableRemapperException(
            /*@NonNull*/ Method method,
            @NonNull Class<? extends ExceptionRemapper> remapper,
            /*@NonNull*/ Throwable cause)
    {
        super(method, cause);
        this.remapper = remapper;
    }

    /**
     * Tells which is the name of the uninstantiable {@link ExceptionRemapper}'s class.
     * @return The name of the uninstantiable {@link ExceptionRemapper}'s class.
     */
    @NonNull
    @TemplateField("R")
    public String getRempperName() {
        return remapper.getSimpleName();
    }
}
