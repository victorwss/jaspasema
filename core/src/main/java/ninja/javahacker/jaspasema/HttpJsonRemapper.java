package ninja.javahacker.jaspasema;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.javalin.http.Context;
import java.lang.reflect.Method;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.badmapping.ExceptionMappingOnReturnException;
import ninja.javahacker.jaspasema.exceptions.http.HttpException;
import ninja.javahacker.jaspasema.processor.AnnotatedMethod;
import ninja.javahacker.jaspasema.processor.JsonTypesProcessor;
import ninja.javahacker.jaspasema.processor.ReturnedOk;

/**
 * @author Victor Williams Stafusa da Silva
 */
public final class HttpJsonRemapper implements ExceptionRemapper {

    /**
     * Sole constructor.
     */
    public HttpJsonRemapper() {
    }

    /**
     * {@inheritDoc}
     * @param method {@inheritDoc}
     * @throws BadServiceMappingException {@inheritDoc}
     */
    @Override
    public void validate(@NonNull AnnotatedMethod<OutputRemapper, ?> method) throws BadServiceMappingException {
        if (method.getAnnotation().on() == ReturnedOk.class) throw new ExceptionMappingOnReturnException(method.getMethod());
    }

    /**
     * {@inheritDoc}
     * @param method {@inheritDoc}
     * @param ctx {@inheritDoc}
     * @param problem {@inheritDoc}
     */
    @NonNull
    @Override
    public void remap(@NonNull Method method, @NonNull Context ctx, @NonNull Object problem) {
        Throwable trouble;
        try {
            trouble = (Throwable) problem;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        }
        var solution = HttpException.convert(method, trouble);
        String json;
        try {
            json = JsonTypesProcessor.writeJson(solution.output());
        } catch (JsonProcessingException e) {
            throw new AssertionError(e);
        }
        if (json == null) json = "";
        ctx.status(solution.getStatusCode());
        ctx.result(json);
        ctx.contentType("text/json;charset=utf-8");
    }
}
