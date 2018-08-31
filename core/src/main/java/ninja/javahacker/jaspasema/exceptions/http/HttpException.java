package ninja.javahacker.jaspasema.exceptions.http;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import lombok.NonNull;
import lombok.Value;
import ninja.javahacker.jaspasema.exceptions.JaspasemaException;
import ninja.javahacker.jaspasema.exceptions.paramvalue.MalformedParameterValueException;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class HttpException extends JaspasemaException {
    private static final long serialVersionUID = 1L;

    private final int statusCode;

    public HttpException(/*@NonNull*/ Method method, int statusCode) {
        super(method);
        this.statusCode = statusCode;
    }

    public HttpException(/*@NonNull*/ Method method, int statusCode, /*@NonNull*/ Throwable cause) {
        super(method, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public static HttpException convert(@NonNull Method method, @NonNull Throwable problem) {
        Throwable solving = problem;
        if (solving instanceof InvocationTargetException) solving = solving.getCause();
        if (solving instanceof UndeclaredThrowableException) solving = solving.getCause();
        if (solving instanceof MalformedParameterValueException) solving = new BadRequestException(method, solving);
        if (!(solving instanceof HttpException)) solving = new UnexpectedHttpException(method, solving);
        return (HttpException) solving;
    }

    @TemplateField("STATUS")
    private String getStatusCodeStr() {
        return String.valueOf(statusCode);
    }

    @TemplateField("CAUSE")
    public String getCauseString() {
        Throwable cause = getCause();
        return cause == null ? "" : cause.toString();
    }

    public Object output() {
        Throwable cause = getCause();
        return cause == null
                ? new ErrorOutput(statusCode, getMessage())
                : new UnexpectedErrorOutput(statusCode, getMessage(), cause.getClass().getName());
    }

    @Value
    public static class ErrorOutput {
        int status;

        @NonNull
        String message;
    }

    @Value
    public static class UnexpectedErrorOutput {
        int status;

        @NonNull
        String message;

        @NonNull
        String exceptionName;
    }
}
