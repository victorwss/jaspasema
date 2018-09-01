package ninja.javahacker.jaspasema.app;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import spark.Request;
import spark.Response;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
@SuppressFBWarnings("IMC_IMMATURE_CLASS_PRINTSTACKTRACE")
public interface RequestErrorLogger {
    public void log(Request rq, Response rp, Throwable error);

    public static final RequestErrorLogger PRINT_STACK_TRACE = (rq, rp, error) -> error.printStackTrace();
}
