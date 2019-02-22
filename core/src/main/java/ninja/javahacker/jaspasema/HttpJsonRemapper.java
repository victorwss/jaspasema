package ninja.javahacker.jaspasema;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.javalin.Context;
import java.lang.reflect.Method;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.badmapping.ExceptionMappingOnReturnException;
import ninja.javahacker.jaspasema.exceptions.http.HttpException;
import ninja.javahacker.jaspasema.processor.JsonTypesProcessor;
import ninja.javahacker.jaspasema.processor.ReturnedOk;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class HttpJsonRemapper implements ExceptionRemapper {

    @Override
    public void validate(
            @NonNull ReifiedGeneric<?> target,
            @NonNull OutputRemapper annotation,
            @NonNull Method method)
            throws BadServiceMappingException
    {
        if (annotation.on() == ReturnedOk.class) throw new ExceptionMappingOnReturnException(method);
    }

    @Override
    public void remap(@NonNull Method method, @NonNull Context ctx, @NonNull Object problem) {
        Throwable trouble;
        try {
            trouble = (Throwable) problem;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        }
        HttpException solution = HttpException.convert(method, trouble);
        String json;
        try {
            json = JsonTypesProcessor.writeJson(false, solution.output());
        } catch (JsonProcessingException e) {
            throw new AssertionError(e);
        }
        ctx.status(solution.getStatusCode());
        ctx.result(json);
        ctx.contentType("text/json;charset=utf-8");
    }
}
