package ninja.javahacker.jaspasema.service;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import java.lang.reflect.InvocationTargetException;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.paramvalue.ParameterValueException;
import ninja.javahacker.jaspasema.exceptions.retvalue.MalformedReturnValueException;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface JaspasemaRoute extends Handler {
    @Override
    public void handle(@NonNull Context ctx)
            throws InvocationTargetException, ParameterValueException, MalformedReturnValueException;
}
