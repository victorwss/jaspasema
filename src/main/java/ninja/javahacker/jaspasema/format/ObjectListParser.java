package ninja.javahacker.jaspasema.format;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.paramvalue.ParameterValueException;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;
import ninja.javahacker.reifiedgeneric.Wrappers;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface ObjectListParser<E> {
    public List<E> make(List<String> in) throws ParameterValueException;

    public static <E> ObjectListParser<E> prepare(
            @NonNull ReifiedGeneric<List<E>> target,
            @NonNull Class<? extends Annotation> annotationClass,
            @NonNull String format,
            @NonNull Parameter p)
            throws BadServiceMappingException
    {
        ReifiedGeneric<E> simple = Wrappers.unwrapIterable(target);
        ParameterParser<E> pp = ParameterParser.prepare(simple, annotationClass, format, p);
        return in -> {
            List<E> r = new ArrayList<>(in.size());
            for (String s : in) {
                r.add(pp.make(s));
            }
            return r;
        };
    }
}