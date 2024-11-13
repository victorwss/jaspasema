package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import lombok.Getter;
import lombok.NonNull;

/**
 * Thrown when some annotation specifies an invalid date/time format.
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class InvalidDateFormatException extends BadServiceMappingException {

    private static final long serialVersionUID = 1L;

    /**
     * What was the annotation that specifies an invalid date/time format.
     * -- GETTER --
     * Tells what was the annotation that specifies an invalid date/time format.
     * @return What was the annotation that specifies an invalid date/time format.
     */
    @NonNull
    private final Class<? extends Annotation> annotation;

    /*
     * What was the invalid date/time format.
     * -- GETTER --
     * Tells what was the invalid date/time format.
     * @return What was the invalid date/time format.
     */
    @NonNull
    private final String format;

    /**
     * Constructs an instance specifying a method as the cause of this exception.
     * @param method The method that is related to this exception.
     * @param annotation What was the annotation that specifies an invalid date/time format.
     * @param format What was the invalid date/time format.
     * @throws IllegalArgumentException If {@code method}, {@code annotation} or {@code format} are {@code null}.
     */
    public InvalidDateFormatException(
            /*@NonNull*/ Method method,
            @NonNull Class<? extends Annotation> annotation,
            @NonNull String format)
    {
        super(method);
        this.annotation = annotation;
        this.format = format;
    }

    /**
     * Constructs an instance specifying a method parameter as the cause of this exception.
     * @param parameter The method parameter that is related to this exception.
     * @param annotation What was the annotation that specifies an invalid date/time format.
     * @param format What was the invalid date/time format.
     * @throws IllegalArgumentException If {@code parameter}, {@code annotation} or {@code format} are {@code null}.
     */
    public InvalidDateFormatException(
            /*@NonNull*/ Parameter parameter,
            @NonNull Class<? extends Annotation> annotation,
            @NonNull String format)
    {
        super(parameter);
        this.annotation = annotation;
        this.format = format;
    }

    /**
     * Tells what was the annotation's name that specifies an invalid date/time format.
     * @return What was the annotation's name that specifies an invalid date/time format.
     */
    @NonNull
    @TemplateField("A")
    public String getAnnotationName() {
        return annotation.getSimpleName();
    }

    /**
     * Tells which was the invalid date/format that triggered this exception.
     * @return Which was the invalid date/format that triggered this exception.
     */
    @NonNull
    @TemplateField("F")
    public String getFormat() {
        return format;
    }
}
