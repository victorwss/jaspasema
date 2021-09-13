package ninja.javahacker.jaspasema.format;

import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.paramvalue.ParameterValueException;
import ninja.javahacker.jaspasema.processor.AnnotatedParameter;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface ObjectListParser<E> {

    @NonNull
    public List<E> make(@NonNull List<String> in) throws ParameterValueException;

    @NonNull
    public static <E> ObjectListParser<E> prepare(
            @NonNull AnnotatedParameter<?, List<E>> param,
            @NonNull String format)
            throws BadServiceMappingException
    {
        var pp = ParameterParser.prepare(AnnotatedParameter.simpler(param), format);
        return in -> {
            List<E> r = new ArrayList<>(in.size());
            for (var s : in) {
                r.add(pp.make(s));
            }
            return r;
        };
    }
}
