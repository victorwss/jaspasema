package ninja.javahacker.jaspasema.ext;

import java.util.NoSuchElementException;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Hosts the {@link #choose(String[])} method.
 * @author Victor Williams Stafusa da Silva
 */
@UtilityClass
public class ObjectUtils {

    /**
     * Returns the first non-{@code null} and non-empty string from the given parameters.
     * If all of them are {@code null} or empty, throws an {@code IllegalArgumentException}.
     * @param objects Some strings to be tested.
     * @return The first non-null and non-empty string from the given parameters.
     * @throws IllegalArgumentException If the {@code objects} is {@code null}.
     * @throws NoSuchElementException If the all of the elements in {@code objects} are {@code null} or empty.
     */
    @NonNull
    public String choose(@NonNull String... objects) {
        for (String t : objects) {
            if (t != null && !t.isEmpty()) return t;
        }
        throw new NoSuchElementException();
    }
}
