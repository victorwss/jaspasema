package ninja.javahacker.jaspasema.exceptions.http;

import java.lang.reflect.Method;
import lombok.ToString;

/**
 * @author Victor Williams Stafusa da Silva
 */
@ToString
public class UnprocessableRequestException extends HttpException {
    private static final long serialVersionUID = 1L;

    public UnprocessableRequestException(/*@NonNull*/ Method method, /*@NonNull*/ Throwable cause) {
        super(method, 422, cause);
    }
}
