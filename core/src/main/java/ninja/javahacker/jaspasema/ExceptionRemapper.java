package ninja.javahacker.jaspasema;

import io.javalin.http.Context;
import java.lang.reflect.Method;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.processor.AnnotatedMethod;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface ExceptionRemapper {
    public void remap(@NonNull Method method, @NonNull Context ctx, @NonNull Object result);

    public default void validate(@NonNull AnnotatedMethod<OutputRemapper, ?> method) throws BadServiceMappingException {
    }
}
