package ninja.javahacker.jaspasema.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;
import ninja.javahacker.reifiedgeneric.Wrappers;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AnnotatedParameter<A extends Annotation, E> {
    @NonNull
    private final ReifiedGeneric<E> target;

    @NonNull
    private final A annotation;

    @NonNull
    private final Parameter parameter;

    @NonNull
    public static <A extends Annotation> AnnotatedParameter<A, ?> of(@NonNull A annotation, @NonNull Parameter parameter) {
        return new AnnotatedParameter<>(ReifiedGeneric.of(parameter.getParameterizedType()), annotation, parameter);
    }

    @NonNull
    public static <A extends Annotation, E> AnnotatedParameter<A, E> simpler(@NonNull AnnotatedParameter<A, List<E>> in) {
        return new AnnotatedParameter<>(Wrappers.unwrapIterable(in.target), in.annotation, in.getParameter());
    }

    @NonNull
    public AnnotatedParameter<A, List<E>> lists() {
        return new AnnotatedParameter<>(Wrappers.list(target), annotation, getParameter());
    }

    @NonNull
    public String getParameterName() {
        return parameter.getName();
    }

    @NonNull
    public Class<? extends Annotation> getAnnotationType() {
        return annotation.annotationType();
    }
}
