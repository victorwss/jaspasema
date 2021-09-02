package ninja.javahacker.jaspasema.exceptions.http;

import java.lang.reflect.Method;
import lombok.ToString;

/**
 * Represents an authorization error (HTTP status code 403).
 * I.e., the client tried to perform an operation in the server, but it bears no authorized credentials for doing so.
 * @author Victor Williams Stafusa da Silva
 */
@ToString
public class NotAllowedException extends HttpException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance specifying which is the offending method.
     * @param method The offending method.
     * @throws IllegalArgumentException If {@code method} is {@code null}.
     */
    public NotAllowedException(/*@NonNull*/ Method method) {
        super(method, 403);
    }
}
