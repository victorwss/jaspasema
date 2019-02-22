package ninja.javahacker.jaspasema.app;

import io.javalin.Context;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface RequestLogger {
    public void log(Context ctx);
}
