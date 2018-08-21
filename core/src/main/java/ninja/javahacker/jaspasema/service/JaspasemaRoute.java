package ninja.javahacker.jaspasema.service;

import java.lang.reflect.InvocationTargetException;
import ninja.javahacker.jaspasema.exceptions.paramvalue.ParameterValueException;
import ninja.javahacker.jaspasema.exceptions.retvalue.MalformedReturnValueException;
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
