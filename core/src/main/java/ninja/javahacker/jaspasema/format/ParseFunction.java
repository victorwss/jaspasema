package ninja.javahacker.jaspasema.format;

import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.function.Function;
import lombok.NonNull;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface ParseFunction<E> {

    @Nullable
    public <X extends Throwable> E parse(@NonNull Function<? super Throwable, X> onError, @NonNull String s) throws X;

    public static boolean accepts(@NonNull ReifiedGeneric<?> target) {
        return FormatterMap.PARSE_MAP.get(target) != null || FormatterMap.PARSE_DT_MAP.get(target) != null;
    }
}