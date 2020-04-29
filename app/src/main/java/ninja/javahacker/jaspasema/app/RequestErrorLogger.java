package ninja.javahacker.jaspasema.app;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.javalin.http.Context;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
@SuppressFBWarnings({"IMC_IMMATURE_CLASS_PRINTSTACKTRACE", "ERRMSG"})
public interface RequestErrorLogger {
    public void log(Context ctx, Throwable error);

    public static final RequestErrorLogger PRINT_STACK_TRACE = (ctx, error) -> error.printStackTrace();
}
