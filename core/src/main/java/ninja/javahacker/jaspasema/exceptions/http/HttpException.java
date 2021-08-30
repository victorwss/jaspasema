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

    public HttpException(/*@NonNull*/ Method method, int statusCode) {
        super(method);
        this.statusCode = statusCode;
    }

    public HttpException(/*@NonNull*/ Method method, int statusCode, /*@NonNull*/ Throwable cause) {
        super(method, cause);
        this.statusCode = statusCode;
    }

    @NonNull
    public static HttpException convert(@NonNull Method method, @NonNull Throwable problem) {
        Throwable solving = problem;
        if (solving instanceof InvocationTargetException) solving = solving.getCause();
        if (solving instanceof UndeclaredThrowableException) solving = solving.getCause();
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

    @Value
    public static class ErrorOutput {
        private final int status;

        @NonNull
        private final String message;
    }

    @Value
    public static class UnexpectedErrorOutput {
        private final int status;

        @NonNull
        private final String message;

        @NonNull
        private final String exceptionName;
    }
}
