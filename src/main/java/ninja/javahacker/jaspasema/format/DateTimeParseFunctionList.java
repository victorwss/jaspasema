package ninja.javahacker.jaspasema.format;

import java.lang.reflect.Parameter;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import lombok.NonNull;
import ninja.javahacker.jaspasema.processor.TargetType;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface DateTimeParseFunctionList<E> {
    public <X extends Throwable> List<E> parse(Function<? super Throwable, X> onError, List<String> s, DateTimeFormatter format) throws X;

    @SuppressWarnings({"unchecked", "element-type-mismatch"})
    public static <E> DateTimeParseFunctionList<E> parserFor(
            @NonNull Parameter p,
            @NonNull TargetType<List<E>> target)
    {
        if (!target.isListType()) return null;
        DateTimeParseFunction<E> func = DateTimeParseFunction.parserFor(TargetType.getListGenericType(target));
        return new DateTimeParseFunctionList<E>() {
            @Override
            public <X extends Throwable> List<E> parse(
                    Function<? super Throwable, X> onError,
                    List<String> list,
                    DateTimeFormatter format)
                    throws X
            {
                List<E> result = new ArrayList<>(list.size());
                for (String s : list) {
                    result.add(func.parse(onError, s, format));
                }
                return result;
            }
        };
    }
}