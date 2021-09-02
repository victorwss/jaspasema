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

    @NonNull
    private final Class<? extends ExceptionRemapper> remapper;

    public RemapperConstructorException(
            /*@NonNull*/ Method method,
            @NonNull Class<? extends ExceptionRemapper> remapper,
            /*@NonNull*/ Throwable cause)
    {
        super(method, cause);
        this.remapper = remapper;
    }

    @NonNull
    @TemplateField("R")
    public String getRempperName() {
        return remapper.getSimpleName();
    }
}
