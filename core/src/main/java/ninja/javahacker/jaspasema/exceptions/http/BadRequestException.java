package ninja.javahacker.jaspasema.exceptions.http;

import java.lang.reflect.Method;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class BadRequestException extends HttpException {
    private static final long serialVersionUID = 1L;

    public BadRequestException(/*@NonNull*/ Method method, /*@NonNull*/ Throwable cause) {
        super(method, 400, cause);
    }
}
