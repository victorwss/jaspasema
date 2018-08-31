package ninja.javahacker.jaspasema;

import java.lang.reflect.Method;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;
import spark.Request;
import spark.Response;

/**
 * @author Victor Williams Stafusa da Silva
 */
public interface ExceptionRemapper {
    public void remap(
            @NonNull Method method,
            @NonNull Request rq,
            @NonNull Response rp,
            @NonNull Object result);

    public default void validate(
            @NonNull ReifiedGeneric<?> target,
            @NonNull OutputRemapper annotation,
            @NonNull Method method)
            throws BadServiceMappingException
    {
    }
}
