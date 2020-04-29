package ninja.javahacker.jaspasema.ext;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Hosts the {@link #choose(String[])} method.
 * @author Victor Williams Stafusa da Silva
 */
@UtilityClass
public class ObjectUtils {

    /**
     * Returns the first non-null and non-empty string from the given parameters.
     * If all of them are null or empty, throws a {@code NullPointerException}.
     * @param objects Some strings to be tested.
     * @return The first non-null and non-empty string from the given parameters.
     */
    @NonNull
    public String choose(@NonNull String... objects) {
        for (String t : objects) {
            if (t != null && !t.isEmpty()) return t;
        }
        throw new NullPointerException();
    }
}
