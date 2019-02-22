package ninja.javahacker.jaspasema.service;

import io.javalin.Context;
import io.javalin.Handler;
import java.lang.reflect.InvocationTargetException;
import ninja.javahacker.jaspasema.exceptions.paramvalue.ParameterValueException;
import ninja.javahacker.jaspasema.exceptions.retvalue.MalformedReturnValueException;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface JaspasemaRoute extends Handler {
    @Override
    public void handle(Context ctx) throws InvocationTargetException, ParameterValueException, MalformedReturnValueException;
}
