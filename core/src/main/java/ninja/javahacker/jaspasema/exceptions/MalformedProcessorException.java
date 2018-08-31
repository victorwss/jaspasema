package ninja.javahacker.jaspasema.exceptions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public abstract class MalformedProcessorException extends JaspasemaException {
    private static final long serialVersionUID = 1L;

    @NonNull
    private final Class<? extends Annotation> badAnnotation;

    @NonNull
    private final Class<?> processorClass;

    protected MalformedProcessorException(
            /*@NonNull*/ Parameter parameter,
            @NonNull Class<? extends Annotation> badAnnotation,
            @NonNull Class<?> processorClass,
            /*@NonNull*/ Throwable cause)
    {
        super(parameter, cause);
        this.badAnnotation = badAnnotation;
        this.processorClass = processorClass;
    }

    protected MalformedProcessorException(
            /*@NonNull*/ Parameter parameter,
            @NonNull Class<? extends Annotation> badAnnotation,
            @NonNull Class<?> processorClass)
    {
        super(parameter);
        this.badAnnotation = badAnnotation;
        this.processorClass = processorClass;
    }

    protected MalformedProcessorException(
            /*@NonNull*/ Method method,
            @NonNull Class<? extends Annotation> badAnnotation,
            @NonNull Class<?> processorClass,
            /*@NonNull*/ Throwable cause)
    {
        super(method, cause);
        this.badAnnotation = badAnnotation;
        this.processorClass = processorClass;
    }

    protected MalformedProcessorException(
            /*@NonNull*/ Method method,
            @NonNull Class<? extends Annotation> badAnnotation,
            @NonNull Class<?> processorClass)
    {
        super(method);
        this.badAnnotation = badAnnotation;
        this.processorClass = processorClass;
    }

    protected MalformedProcessorException(
            /*@NonNull*/ Class<?> declaringClass,
            @NonNull Class<? extends Annotation> badAnnotation,
            @NonNull Class<?> processorClass,
            /*@NonNull*/ Throwable cause)
    {
        super(declaringClass, cause);
        this.badAnnotation = badAnnotation;
        this.processorClass = processorClass;
    }

    protected MalformedProcessorException(
            /*@NonNull*/ Class<?> declaringClass,
            @NonNull Class<? extends Annotation> badAnnotation,
            @NonNull Class<?> processorClass)
    {
        super(declaringClass);
        this.badAnnotation = badAnnotation;
        this.processorClass = processorClass;
    }

    @TemplateField("A")
    public String getAnnotationName() {
        return getBadAnnotation().getSimpleName();
    }

    @TemplateField("R")
    public String getRempperName() {
        return processorClass.getSimpleName();
    }
}
