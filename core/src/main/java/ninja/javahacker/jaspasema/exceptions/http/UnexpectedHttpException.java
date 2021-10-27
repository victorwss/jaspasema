package ninja.javahacker.jaspasema.exceptions.http;

import lombok.ToString;

/**
 * Represents an internal server error (HTTP status code 500).
 * I.e., something unexpected and bad occurred in the server and the client should be made aware of that.
 * @author Victor Williams Stafusa da Silva
 */
@ToString
public class UnexpectedHttpException extends HttpException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance specifying which is the offending method and what was the raised error.
     * @param cause The raised error.
     * @throws IllegalArgumentException If {@code cause} is {@code null}.
     */
    public UnexpectedHttpException(/*@NonNull*/ Throwable cause) {
        super(500, cause);
    }
}
