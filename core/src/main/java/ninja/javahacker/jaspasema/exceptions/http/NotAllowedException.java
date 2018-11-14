package ninja.javahacker.jaspasema.exceptions.http;

import java.lang.reflect.Method;
import lombok.ToString;

/**
 * @author Victor Williams Stafusa da Silva
 */
@ToString
public class NotAllowedException extends HttpException {
    private static final long serialVersionUID = 1L;

    public NotAllowedException(/*@NonNull*/ Method method) {
        super(method, 403);
    }
}
