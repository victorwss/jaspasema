package ninja.javahacker.jaspasema.exceptions.http;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Method;

/**
 * @author Victor Williams Stafusa da Silva
 */
@SuppressFBWarnings("FCCD_FIND_CLASS_CIRCULAR_DEPENDENCY")
public class UnexpectedHttpException extends HttpException {
    private static final long serialVersionUID = 1L;

    public UnexpectedHttpException(/*@NonNull*/ Method method, /*@NonNull*/ Throwable cause) {
        super(method, 500, cause);
    }
}
