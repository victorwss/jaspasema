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
 * Packages information about some parameter, its type and its relevant annotation.
 * @param <A> The type of the annotation.
 * @param <E> The type of the parameter.
 * @author Victor Williams Stafusa da Silva
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class AnnotatedParameter<A extends Annotation, E> {

    /**
     * The parameter's type.
     * -- GETTER --
     * Returns The parameter's type.
     * @return The parameter's type.
     */
    @NonNull
    private final ReifiedGeneric<E> target;

    /**
     * The parameter's relevant annotation.
     * -- GETTER --
     * Returns The parameter's relevant annotation.
     * @return The parameter's relevant annotation.
     */
    @NonNull
    private final A annotation;

    /**
     * The target parameter.
     * -- GETTER --
     * Returns The target parameter.
     * @return The target parameter.
     */
    @NonNull
    private final Parameter parameter;

    /**
     * Creates an instance of {@code AnnotatedParameter} from a given parameter and its relevant annotation.
     * @param <A> The type of the relevant annotation.
     * @param annotation Some annotation applied to the paremeter.
     * @param parameter The given parameter.
     * @return An instance of {@code AnnotatedParameter} from the given annotation and the given parameter.
     */
    @NonNull
    public static <A extends Annotation> AnnotatedParameter<A, ?> of(@NonNull A annotation, @NonNull Parameter parameter) {
        return new AnnotatedParameter<>(ReifiedGeneric.of(parameter.getParameterizedType()), annotation, parameter);
    }

    /**
     * Unwraps an {@code AnnotatedParameter} of a list giving an {@code AnnotatedParameter} of its base type.
     * @param <A> The type of the relevant annotation to unwrap.
     * @param <E> The type of the list.
     * @param in The  {@code AnnotatedParameter} to unwrap.
     * @return An {@code AnnotatedParameter} of the list base type.
     */
    @NonNull
    public static <A extends Annotation, E> AnnotatedParameter<A, E> simpler(@NonNull AnnotatedParameter<A, List<E>> in) {
        return new AnnotatedParameter<>(Wrappers.unwrapIterable(in.target), in.annotation, in.getParameter());
    }

    /**
     * Gives an {@code AnnotatedParameter} of a list of the current parameter.
     * @return An {@code AnnotatedParameter} of a list of the current parameter.
     */
    @NonNull
    public AnnotatedParameter<A, List<E>> lists() {
        return new AnnotatedParameter<>(Wrappers.list(target), annotation, getParameter());
    }

    /**
     * Returns The parameter's name.
     * @return The parameter's name.
     */
    @NonNull
    public String getParameterName() {
        return parameter.getName();
    }

    /**
     * Returns The parameter's relevant annotation's type.
     * @return The parameter's relevant annotation's type.
     */
    @NonNull
    @SuppressWarnings("unchecked")
    public Class<A> getAnnotationType() {
        return (Class<A>) annotation.annotationType();
    }
}
