package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Method;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.ExceptionRemapper;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class UninstantiableRemapperException extends BadServiceMappingException {
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_TEMPLATE =
            "The exception remapper $R$ is not an instantiable class.";

    @NonNull
    private final Class<? extends ExceptionRemapper> remapper;

    protected UninstantiableRemapperException(
            /*@NonNull*/ Method method,
            /*@NonNull*/ Class<? extends ExceptionRemapper> remapper,
            /*@NonNull*/ Throwable cause)
    {
        super(method, MESSAGE_TEMPLATE.replace("$R$", remapper.getSimpleName()), cause);
        this.remapper = remapper;
    }

    public static UninstantiableRemapperException create(
            @NonNull Method method,
            @NonNull Class<? extends ExceptionRemapper> remapper,
            @NonNull Throwable cause)
    {
        return new UninstantiableRemapperException(method, remapper, cause);
    }
}
