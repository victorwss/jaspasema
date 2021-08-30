package ninja.javahacker.jaspasema.exceptions.http;

import java.lang.reflect.Method;
import lombok.ToString;

/**
 * Represents a bad rquest error (HTTP status code 400).
 * I.e., the client tried to perform an operation in the server, but the sent request was structurally malformed.
 * @author Victor Williams Stafusa da Silva
 */
@ToString
public class BadRequestException extends HttpException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance specifying which is the offending method and what was the raised error.
     * @param method The offending method.
     * @param cause The raised error.
     */
    public BadRequestException(/*@NonNull*/ Method method, /*@NonNull*/ Throwable cause) {
        super(method, 400, cause);
    }
}
