package ninja.javahacker.jaspasema.exceptions.http;

import java.io.Serializable;
import lombok.Getter;
import lombok.NonNull;

/**
 * Serialization (for JSON, mainly) of an exception with the actual exception name.
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public final class ErrorOutput implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The HTTP status code.
     * -- GETTER --
     * Retrieves the HTTP status code.
     * @return The HTTP status code.
     */
    private final int status;

    /**
     * The error message.
     * -- GETTER --
     * Retrieves the error message.
     * @return The error message.
     */
    @NonNull
    private final String message;

    /**
     * The cause exception name.
     * -- GETTER --
     * Retrieves the cause exception name.
     * @return The cause exception name.
     */
    @NonNull
    private final String exceptionCauseName;

    /**
     * Creates an instance.
     * @param status The HTTP status code.
     * @param message The error message.
     * @param exceptionCauseName The cause exception name.
     * @throws IllegalArgumentException If {@code message} or {@code exceptionName} are {@code null}.
     */
    public ErrorOutput(int status, @NonNull String message, @NonNull String exceptionCauseName) {
        this.status = status;
        this.message = message;
        this.exceptionCauseName = exceptionCauseName;
    }
}
