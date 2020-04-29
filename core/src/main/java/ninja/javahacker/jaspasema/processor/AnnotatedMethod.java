package ninja.javahacker.jaspasema.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Value
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class AnnotatedMethod<A extends Annotation, E> {
    @NonNull
    private final ReifiedGeneric<E> target;

    @NonNull
    private final A annotation;

    @NonNull
    private final Method method;

    @NonNull
    public String getMethodName() {
        return method.getName();
    }

    @NonNull
    public Class<? extends Annotation> getAnnotationType() {
        return annotation.annotationType();
    }

    @NonNull
    public static <A extends Annotation, E> AnnotatedMethod<A, E> of(
            @NonNull ReifiedGeneric<E> target,
            @NonNull A a,
            @NonNull Method m)
    {
        return new AnnotatedMethod<>(target, a, m);
    }

    @NonNull
    public static <A extends Annotation> AnnotatedMethod<A, ?> of(
            @NonNull A a,
            @NonNull Method m)
    {
        return new AnnotatedMethod<>(ReifiedGeneric.of(m.getGenericReturnType()), a, m);
    }

    @NonNull
    public static <A extends Annotation, X extends Throwable> AnnotatedMethod<A, X> ofException(
            @NonNull ReifiedGeneric<X> target,
            @NonNull A a,
            @NonNull Method m)
    {
        return new AnnotatedMethod<>(target, a, m);
    }
}
