package ninja.javahacker.jaspasema.format;

import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface FormatterFunction<E> {

    @Nullable
    public String format(@Nullable E value);

}