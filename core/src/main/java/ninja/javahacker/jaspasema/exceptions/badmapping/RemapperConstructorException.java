package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Method;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.ExceptionRemapper;

/**
 * Thrown when an attempt to construct an instance of some {@link ExceptionRemapper} fails because the
 * {@link ExceptionRemapper}'s constructor threw an exception.
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class RemapperConstructorException extends BadServiceMappingException {

    private static final long serialVersionUID = 1L;

    /**
     * The {@link ExceptionRemapper}'s class that could be instantiated.
     * -- GETTER --
     * Tells which is the {@link ExceptionRemapper}'s class that could be instantiated.
     * @return Which is the {@link ExceptionRemapper}'s class that could be instantiated.
     */
    @NonNull
    private final Class<? extends ExceptionRemapper> remapper;

    /**
     * Creates an instance specifying which is the offending method.
     * @param method The offending method.
     * @param remapper The {@link ExceptionRemapper}'s class.
     * @param cause The exception raised by the {@link ExceptionRemapper}'s class constructor.
     * @throws IllegalArgumentException If {@code method}, {@code remapper} or {@code cause} are {@code null}.
     */
    public RemapperConstructorException(
            /*@NonNull*/ Method method,
            @NonNull Class<? extends ExceptionRemapper> remapper,
            /*@NonNull*/ Throwable cause)
    {
        super(method, cause);
        this.remapper = remapper;
    }

    /**
     * Tells which is the name of the {@link ExceptionRemapper}'s class that could be instantiated.
     * @return The name of the {@link ExceptionRemapper}'s class that could be instantiated.
     */
    @NonNull
    @TemplateField("R")
    public String getRempperName() {
        return remapper.getSimpleName();
    }
}
