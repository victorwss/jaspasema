package ninja.javahacker.jaspasema.service;

import java.lang.reflect.InvocationTargetException;
import ninja.javahacker.jaspasema.exceptions.ParameterValueException;
import ninja.javahacker.jaspasema.exceptions.MalformedReturnValueException;
import spark.Request;
import spark.Response;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface JaspasemaRoute {
    public void handleIt(Request rq, Response rp)
            throws InvocationTargetException, ParameterValueException, MalformedReturnValueException;
}
