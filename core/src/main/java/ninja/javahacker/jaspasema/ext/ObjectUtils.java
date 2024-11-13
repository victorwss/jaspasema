package ninja.javahacker.jaspasema.ext;

import java.util.NoSuchElementException;
import lombok.NonNull;

/**
 * Hosts the {@link #choose(String[])} method.
 * @author Victor Williams Stafusa da Silva
 */
public class ObjectUtils {

    private ObjectUtils() {
        throw new UnsupportedOperationException("Can't instantiate.");
    }

    /**
     * Returns the first non-{@code null} and non-empty string from the given parameters.
     * If all of them are {@code null} or empty, throws a {@code NoSuchElementException}.
     * @param objects Some strings to be tested.
     * @return The first non-null and non-empty string from the given parameters.
     * @throws IllegalArgumentException If the {@code objects} is {@code null}.
     * @throws NoSuchElementException If the all of the elements in {@code objects} are {@code null} or empty.
     */
    @NonNull
    public static String choose(@NonNull String... objects) {
        for (var t : objects) {
            if (t != null && !t.isEmpty()) return t;
        }
        throw new NoSuchElementException();
    }
}
