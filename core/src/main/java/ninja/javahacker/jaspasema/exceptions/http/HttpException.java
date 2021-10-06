package ninja.javahacker.jaspasema.exceptions.http;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import ninja.javahacker.jaspasema.exceptions.JaspasemaException;
import ninja.javahacker.jaspasema.exceptions.paramvalue.MalformedParameterValueException;

/**
 * Used to represent an exception that should carry off an HTTP status code.
 * @author Victor Williams Stafusa da Silva
 */
@ToString
public class HttpException extends JaspasemaException {

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
     * @param method The method that is related to this exception.
     * @param statusCode The HTTP status code for this exception.
     * @throws IllegalArgumentException If {@code method} is {@code null}.
     */
    public HttpException(/*@NonNull*/ Method method, int statusCode) {
        super(method);
        this.statusCode = statusCode;
    }

    /**
     * Constructs an instance specifiying both a method and another exception as the cause of this exception.
     * @param method The method that is related to this exception.
     * @param statusCode The HTTP status code for this exception.
     * @param cause Another exception that is the cause of this exception.
     * @throws IllegalArgumentException If {@code method} or {@code cause} are {@code null}.
     */
    public HttpException(/*@NonNull*/ Method method, int statusCode, /*@NonNull*/ Throwable cause) {
        super(method, cause);
        this.statusCode = statusCode;
    }

    /**
     * Produces an instance of {@code HttpException} that better represents the given exception {@code problem},
     * unwrapping and wrapping intermediate exceptions if needed.
     * @param method The method which caused the problem.
     * @param problem The exception to be unwrapped or wrapped if needed in a {@code HttpException}.
     * @return An instance of {@code HttpException} that better represents the given exception {@code problem},
     *     unwrapping and wrapping intermediate exceptions if needed.
     * @throws IllegalArgumentException If {@code method} or {@code problem} are {@code null}.
     */
    @NonNull
    @SuppressFBWarnings("ITC_INHERITANCE_TYPE_CHECKING")
    public static HttpException convert(@NonNull Method method, @NonNull Throwable problem) {
        if (problem instanceof InvocationTargetException || problem instanceof UndeclaredThrowableException) {
            return convert(method, problem.getCause());
        }
        if (problem instanceof HttpException) return (HttpException) problem;
        if (problem instanceof MalformedParameterValueException) return new BadRequestException(method, problem);
        return new UnexpectedHttpException(method, problem);
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
}
