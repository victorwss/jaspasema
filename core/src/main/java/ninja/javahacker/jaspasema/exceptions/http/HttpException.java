package ninja.javahacker.jaspasema.exceptions.http;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;
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

    @NonNull
    public static HttpException convert(@NonNull Method method, @NonNull Throwable problem) {
        Throwable solving = problem;
        if (solving instanceof InvocationTargetException || solving instanceof UndeclaredThrowableException) solving = solving.getCause();
        if (solving instanceof MalformedParameterValueException) solving = new BadRequestException(method, solving);
        if (!(solving instanceof HttpException)) solving = new UnexpectedHttpException(method, solving);
        return (HttpException) solving;
    }

    @NonNull
    @TemplateField("STATUS")
    public String getStatusCodeString() {
        return String.valueOf(statusCode);
    }

    @NonNull
    @TemplateField("CAUSE")
    public String getCauseString() {
        Throwable cause = getCause();
        return cause == null ? "" : cause.toString();
    }

    @NonNull
    public Object output() {
        Throwable cause = getCause();
        return cause == null
                ? new ErrorOutput(statusCode, getMessage())
                : new UnexpectedErrorOutput(statusCode, getMessage(), cause.getClass().getName());
    }

    /**
     * Serialization (for JSON, mainly) of an exception without the actual exception name.
     * @author Victor Williams Stafusa da Silva
     */
    @Value
    public static class ErrorOutput {

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
    }

    /**
     * Serialization (for JSON, mainly) of an exception with the actual exception name.
     * @author Victor Williams Stafusa da Silva
     */
    @Value
    public static class UnexpectedErrorOutput {

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
         * The exception name.
         * -- GETTER --
         * Retrieves the exception name.
         * @return The exception name.
         */
        @NonNull
        private final String exceptionName;
    }
}
