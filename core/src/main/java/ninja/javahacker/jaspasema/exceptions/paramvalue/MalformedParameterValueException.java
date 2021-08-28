package ninja.javahacker.jaspasema.exceptions.paramvalue;

import edu.umd.cs.findbugs.annotations.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.function.Function;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.processor.AnnotatedParameter;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class MalformedParameterValueException extends ParameterValueException {
    private static final long serialVersionUID = 1L;

    @NonNull
    private final Class<? extends Annotation> annotation;

    @Nullable
    private final String rawValue;

    public MalformedParameterValueException(
            /*@NonNull*/ Parameter parameter,
            @NonNull Class<? extends Annotation> annotation,
            @Nullable String rawValue,
            /*@NonNull*/ Throwable cause)
    {
        super(parameter, cause);
        this.annotation = annotation;
        this.rawValue = rawValue;
    }

    public static Function<Throwable, MalformedParameterValueException> expectingCause(
            @NonNull AnnotatedParameter<?, ?> param,
            @Nullable String rawValue)
    {
        return cause -> new MalformedParameterValueException(param.getParameter(), param.getAnnotationType(), rawValue, cause);
    }

    @NonNull
    @TemplateField("A")
    public String getAnnotationName() {
        return annotation.getSimpleName();
    }

    @Nullable
    @TemplateField("V")
    public String getRawValue() {
        return rawValue;
    }
}