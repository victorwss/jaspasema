package ninja.javahacker.jaspasema.exceptions.http;

import java.lang.reflect.Method;
import lombok.ToString;

/**
 * @author Victor Williams Stafusa da Silva
 */
@ToString
public class BadRequestException extends HttpException {
    private static final long serialVersionUID = 1L;

    public BadRequestException(/*@NonNull*/ Method method, /*@NonNull*/ Throwable cause) {
        super(method, 400, cause);
    }
}
