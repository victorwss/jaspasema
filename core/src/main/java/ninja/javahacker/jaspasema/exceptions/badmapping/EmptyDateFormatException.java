package ninja.javahacker.jaspasema.exceptions.badmapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;
import lombok.Getter;
import lombok.NonNull;

/**
 * Thrown when {@link LocalDate}, {@link LocalDateTime}, {@link LocalTime}, {@link Year} or {@link YearMonth}
 * are used as a parameter or return type with an annotation that should, but does not, specify the date format.
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class EmptyDateFormatException extends BadServiceMappingException {

    private static final long serialVersionUID = 1L;

    /**
     * What was the annotation that is missing the date/time format.
     * -- GETTER --
     * Tells what was the annotation that is missing the date/time format.
     * @return What was the annotation that is missing the date/time format.
     */
    @NonNull
    private final Class<? extends Annotation> annotation;

    /**
     * Constructs an instance specifying a method as the cause of this exception.
     * @param method The method that is related to this exception.
     * @param annotation What was the annotation that is missing the date/time format.
     * @throws IllegalArgumentException If {@code method} or {@code annotation} are {@code null}.
     */
    public EmptyDateFormatException(
            /*@NonNull*/ Method method,
            @NonNull Class<? extends Annotation> annotation)
    {
        super(method);
        this.annotation = annotation;
    }

    /**
     * Constructs an instance specifying a method parameter as the cause of this exception.
     * @param parameter The method parameter that is related to this exception.
     * @param annotation What was the annotation that is missing the date/time format.
     * @throws IllegalArgumentException If {@code parameter} or {@code annotation} are {@code null}.
     */
    public EmptyDateFormatException(
            /*@NonNull*/ Parameter parameter,
            @NonNull Class<? extends Annotation> annotation)
    {
        super(parameter);
        this.annotation = annotation;
    }

    /**
     * Tells what was the name of the annotation that is missing the date/time format.
     * @return What was the name of the annotation that is missing the date/time format.
     */
    @NonNull
    @TemplateField("A")
    public String getAnnotationName() {
        return annotation.getSimpleName();
    }
}
