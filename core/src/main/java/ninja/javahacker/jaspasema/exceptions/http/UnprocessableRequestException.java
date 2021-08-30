package ninja.javahacker.jaspasema.exceptions.http;

import java.lang.reflect.Method;
import lombok.ToString;

/**
 * Represents an data input error (HTTP status code 422).
 * I.e., the client tried to perform an operation in the server,
 * but the input data, albeit structurally well-formed, makes no sense.
 * @author Victor Williams Stafusa da Silva
 */
@ToString
public class UnprocessableRequestException extends HttpException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance specifying which is the offending method and what was the raised error.
     * @param method The offending method.
     * @param cause The raised error.
     */
    public UnprocessableRequestException(/*@NonNull*/ Method method, /*@NonNull*/ Throwable cause) {
        super(method, 422, cause);
    }
}
