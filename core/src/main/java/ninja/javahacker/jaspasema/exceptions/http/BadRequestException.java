package ninja.javahacker.jaspasema.exceptions.http;

import lombok.ToString;

/**
 * Represents a bad request error (HTTP status code 400).
 * I.e., the client tried to perform an operation in the server, but the sent request was structurally malformed.
 * @author Victor Williams Stafusa da Silva
 */
@ToString
public class BadRequestException extends HttpException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance specifying which is the offending method and what was the raised error.
     * @param cause The raised error.
     * @throws IllegalArgumentException If {@code cause} is {@code null}.
     */
    public BadRequestException(/*@NonNull*/ Throwable cause) {
        super(400, cause);
    }
}
