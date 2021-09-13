package ninja.javahacker.jaspasema.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * Packages information about some method, its return type and its relevant annotation.
 * @author Victor Williams Stafusa da Silva
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class AnnotatedMethod<A extends Annotation, E> {

    /**
     * The method's return type.
     * -- GETTER --
     * Returns The method's return type.
     * @return The method's return type.
     */
    @NonNull
    private final ReifiedGeneric<E> target;

    /**
     * The method's relevant annotation.
     * -- GETTER --
     * Returns The method's relevant annotation.
     * @return The method's relevant annotation.
     */
    @NonNull
    private final A annotation;

    /**
     * The target method.
     * -- GETTER --
     * Returns The target method.
     * @return The target method.
     */
    @NonNull
    private final Method method;

    /**
     * Returns The target method's name.
     * @return The target method's name.
     */
    @NonNull
    public String getMethodName() {
        return method.getName();
    }

    /**
     * Returns The method's relevant annotation's type.
     * @return The method's relevant annotation's type.
     */
    @NonNull
    @SuppressWarnings("unchecked")
    public Class<A> getAnnotationType() {
        return (Class<A>) annotation.annotationType();
    }

    /**
     * Creates an intance of {@code AnnotatedMethod} from a given method, its return type, its relevant annotation and accompaning generics.
     * @param <A> The type of the relevant annotation.
     * @param <E> The return type of the method.
     * @param target The return type of the method.
     * @param annotation Some annotation applied to the method.
     * @param method Some method.
     * @return An instance packaging the method and its relevant annotation.
     */
    @NonNull
    public static <A extends Annotation, E> AnnotatedMethod<A, E> of(
            @NonNull ReifiedGeneric<E> target,
            @NonNull A annotation,
            @NonNull Method method)
    {
        return new AnnotatedMethod<>(target, annotation, method);
    }

    /**
     * Creates an intance given a method, its relevant annotation and accompaning generics.
     * @param <A> The type of the relevant annotation.
     * @param annotation Some annotation applied to the method.
     * @param method Some method.
     * @return An instance packaging the method and its relevant annotation.
     */
    @NonNull
    public static <A extends Annotation> AnnotatedMethod<A, ?> of(
            @NonNull A annotation,
            @NonNull Method method)
    {
        return new AnnotatedMethod<>(ReifiedGeneric.of(method.getGenericReturnType()), annotation, method);
    }

    /**
     * Creates an intance given a method (mapped by one of its exception types instead of return type),
     * its relevant annotation and accompaning generics.
     * @param <A> The type of the relevant annotation.
     * @param <X> The type of the exception.
     * @param target The exception type of the method.
     * @param annotation Some annotation applied to the method.
     * @param method Some method.
     * @return An instance packaging the method and its relevant annotation.
     */
    @NonNull
    public static <A extends Annotation, X extends Throwable> AnnotatedMethod<A, X> ofException(
            @NonNull ReifiedGeneric<X> target,
            @NonNull A annotation,
            @NonNull Method method)
    {
        return new AnnotatedMethod<>(target, annotation, method);
    }
}
