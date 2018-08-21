package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.reflect.Method;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.ExceptionRemapper;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class RemapperConstructorException extends BadServiceMappingException {
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_TEMPLATE =
            "Remapper constructor of class $R$ throwed an exception.";

    @NonNull
    private final Class<? extends ExceptionRemapper> remapper;

    protected RemapperConstructorException(
            /*@NonNull*/ Method method,
            /*@NonNull*/ Class<? extends ExceptionRemapper> remapper,
            /*@NonNull*/ Throwable cause)
    {
        super(method, MESSAGE_TEMPLATE.replace("$R$", remapper.getSimpleName()), cause);
        this.remapper = remapper;
    }

    public static RemapperConstructorException create(
            @NonNull Method method,
            @NonNull Class<? extends ExceptionRemapper> remapper,
            @NonNull Throwable cause)
    {
        return new RemapperConstructorException(method, remapper, cause);
    }
}
