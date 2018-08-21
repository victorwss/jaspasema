package ninja.javahacker.jaspasema.app;

import spark.Request;
import spark.Response;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface RequestErrorLogger {
    public void log(Request rq, Response rp, Throwable error);
}
