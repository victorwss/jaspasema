package ninja.javahacker.jaspasema.exceptions.http;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import ninja.javahacker.jaspasema.exceptions.messages.ExceptionTemplate;
import ninja.javahacker.jaspasema.exceptions.messages.TemplateField;
import ninja.javahacker.jaspasema.exceptions.paramvalue.MalformedParameterValueException;

/**
 * Used to represent an exception that should carry off an HTTP status code.
 * @author Victor Williams Stafusa da Silva
 */
@ToString
public class HttpException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * The HTTP status code for this exception.
     * -- GETTER --
     * Provides the HTTP status code for this exception.
     * @return The HTTP status code for this exception.
     */
    @Getter
    private final int statusCode;

    /**
     * Constructs an instance specifiying a method as the cause of this exception.
     * @param statusCode The HTTP status code for this exception.
     */
    public HttpException(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Constructs an instance specifiying both a method and another exception as the cause of this exception.
     * @param statusCode The HTTP status code for this exception.
     * @param cause Another exception that is the cause of this exception.
     * @throws IllegalArgumentException If {@code cause} is {@code null}.
     */
    public HttpException(int statusCode, @NonNull Throwable cause) {
        super(cause);
        this.statusCode = statusCode;
    }

    /**
     * Constructs an instance specifiying a method as the cause of this exception.
     * @param statusCode The HTTP status code for this exception.
     * @param message The exception message.
     */
    public HttpException(int statusCode, @NonNull String message) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     * Constructs an instance specifiying both a method and another exception as the cause of this exception.
     * @param statusCode The HTTP status code for this exception.
     * @param cause Another exception that is the cause of this exception.
     * @param message The exception message.
     * @throws IllegalArgumentException If {@code cause} is {@code null}.
     */
    public HttpException(int statusCode, @NonNull String message, @NonNull Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    /**
     * Produces an instance of {@code HttpException} that better represents the given exception {@code problem},
     * unwrapping and wrapping intermediate exceptions if needed.
     * @param problem The exception to be unwrapped or wrapped if needed in a {@code HttpException}.
     * @return An instance of {@code HttpException} that better represents the given exception {@code problem},
     *     unwrapping and wrapping intermediate exceptions if needed.
     * @throws IllegalArgumentException If {@code method} or {@code problem} are {@code null}.
     */
    @NonNull
    @SuppressFBWarnings("ITC_INHERITANCE_TYPE_CHECKING")
    public static HttpException convert(@NonNull Throwable problem) {
        if (problem instanceof InvocationTargetException || problem instanceof UndeclaredThrowableException) {
            return convert(problem.getCause());
        }
        if (problem instanceof HttpException) return (HttpException) problem;
        if (problem instanceof MalformedParameterValueException) return new BadRequestException(problem);
        return new UnexpectedHttpException(problem);
    }

    /**
     * Provides the HTTP status code for this exception.
     * @return The HTTP status code for this exception.
     */
    @NonNull
    @TemplateField("STATUS")
    public String getStatusCodeString() {
        return String.valueOf(statusCode);
    }

    /**
     * Creates an object representing this exception in a format easily serializable to JSON.
     * @return An object representing this exception in a format easily serializable to JSON.
     */
    @NonNull
    public Object output() {
        Throwable cause = getCause();
        return new ErrorOutput(statusCode, getMessage(), cause == null ? "" : cause.getClass().getName());
    }

    /**
     * Do not call this method.
     * This was overriden from the {@link Throwable} class in order to be disallowed.
     * Subclasses that feature a cause must set it through the superclass constructor only.
     * @param cause The cause exception used to set as a cause for this exception. However, since this method is not
     *     supported and should not be used, this parameter is not used.
     * @return Never returns normally.
     * @throws UnsupportedOperationException Always.
     * @deprecated Do not call this method.
     */
    @Override
    @Deprecated
    public final HttpException initCause(Throwable cause) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the message of the causing exception or an empty string if there isn't any.
     * @return The message of the causing exception or an empty string if there isn't any.
     */
    @NonNull
    @TemplateField("CAUSE")
    public String getCauseString() {
        Throwable cause = getCause();
        return cause == null ? "" : cause.toString();
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String getMessage() {
        String sm = super.getMessage();
        if (sm != null) return sm;
        return ExceptionTemplate.getExceptionTemplate().templateFor(this);
    }
}
