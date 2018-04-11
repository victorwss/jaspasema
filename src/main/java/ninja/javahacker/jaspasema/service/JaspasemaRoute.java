package ninja.javahacker.jaspasema.service;

import java.lang.reflect.InvocationTargetException;
import ninja.javahacker.jaspasema.processor.MalformedParameterException;
import ninja.javahacker.jaspasema.processor.MalformedReturnValueException;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface JaspasemaRoute extends Route {
    @Override
    public default String handle(
            Request rq,
            Response rp)
            throws InvocationTargetException,
            MalformedParameterException,
            MalformedReturnValueException
    {
        handleIt(rq, rp);
        return rp.body();
    }

    public void handleIt(
            Request rq,
            Response rp)
            throws InvocationTargetException,
            MalformedParameterException,
            MalformedReturnValueException;
}
