package ninja.javahacker.jaspasema.app;

import io.javalin.http.Context;

/**
 * Logs HTTP requests and/or responses.
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface RequestLogger {

    /**
     * Logs some HTTP request and/or response.
     * @param ctx The HTTP request and/or response.
     */
    public void log(Context ctx);
}
