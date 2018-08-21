package ninja.javahacker.jaspasema.exceptions.paramvalue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class MalformedParameterValueException extends ParameterValueException {
    private static final long serialVersionUID = 1L;

    public static final String TEMPLATE = "The value \"$V$\" is invalid for a @$A$-annotated parameter.";

    @NonNull
    private final Class<? extends Annotation> annotation;

    @NonNull
    private final String rawValue;

    protected MalformedParameterValueException(
            /*@NonNull*/ Parameter parameter,
            /*@NonNull*/ Class<? extends Annotation> annotation,
            /*@NonNull*/ String rawValue,
            /*@NonNull*/ Throwable cause)
    {
        super(parameter, TEMPLATE.replace("$A$", annotation.getSimpleName()).replace("$V$", rawValue), cause);
        this.annotation = annotation;
        this.rawValue = rawValue;
    }

    public static MalformedParameterValueException create(
            @NonNull Parameter parameter,
            @NonNull Class<? extends Annotation> annotation,
            @NonNull String rawValue,
            @NonNull Throwable cause)
    {
        return new MalformedParameterValueException(parameter, annotation, rawValue, cause);
    }
}