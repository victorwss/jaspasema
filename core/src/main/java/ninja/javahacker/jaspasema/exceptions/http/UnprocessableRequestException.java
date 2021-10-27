package ninja.javahacker.jaspasema.exceptions.http;

import lombok.ToString;

/**
 * Represents a data input error (HTTP status code 422).
 * I.e., the client tried to perform an operation in the server,
 * but the input data, albeit structurally well-formed, makes no sense.
 * @author Victor Williams Stafusa da Silva
 */
@ToString
public class UnprocessableRequestException extends HttpException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance specifying which is the offending method and what was the raised error.
     * @param cause The raised error.
     * @throws IllegalArgumentException If {@code cause} is {@code null}.
     */
    public UnprocessableRequestException(/*@NonNull*/ Throwable cause) {
        super(422, cause);
    }
}
