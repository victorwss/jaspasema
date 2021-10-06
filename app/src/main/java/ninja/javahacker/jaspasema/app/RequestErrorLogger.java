package ninja.javahacker.jaspasema.app;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.javalin.http.Context;

/**
 * Logs HTTP requests and/or responses that produced some errors.
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
@SuppressFBWarnings({"IMC_IMMATURE_CLASS_PRINTSTACKTRACE", "ERRMSG"})
public interface RequestErrorLogger {

    /**
     * Logs some HTTP request and/or response that produced some error.
     * @param ctx The HTTP request and/or response that produced some error.
     * @param error The error to be logged.
     */
    public void log(Context ctx, Throwable error);

    /**
     * Default error logger which just calls {@link Throwable#printStackTrace()}.
     */
    public static final RequestErrorLogger PRINT_STACK_TRACE = (ctx, error) -> error.printStackTrace();
}
