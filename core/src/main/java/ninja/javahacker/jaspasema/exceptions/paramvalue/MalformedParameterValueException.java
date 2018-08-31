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

    @NonNull
    private final Class<? extends Annotation> annotation;

    @NonNull
    private final String rawValue;

    public MalformedParameterValueException(
            /*@NonNull*/ Parameter parameter,
            @NonNull Class<? extends Annotation> annotation,
            @NonNull String rawValue,
            /*@NonNull*/ Throwable cause)
    {
        super(parameter, cause);
        this.annotation = annotation;
        this.rawValue = rawValue;
    }

    @TemplateField("A")
    public String getAnnotationName() {
        return annotation.getSimpleName();
    }

    @TemplateField("V")
    public String getRawValue() {
        return rawValue;
    }
}