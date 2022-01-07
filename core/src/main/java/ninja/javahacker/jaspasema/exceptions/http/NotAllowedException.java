package ninja.javahacker.jaspasema.exceptions.http;

import lombok.NonNull;
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
     * Simple constructor.
     */
    public NotAllowedException() {
        super(403);
    }

    /**
     * Constructor featuring a custom message.
     * @param message The detail message.
     */
    public NotAllowedException(@NonNull String message) {
        super(403, message);
    }
}
