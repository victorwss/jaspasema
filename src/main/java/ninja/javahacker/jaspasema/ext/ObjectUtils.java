package ninja.javahacker.jaspasema.ext;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * @author Victor Williams Stafusa da Silva
 */
@UtilityClass
public class ObjectUtils {
    @NonNull
    public String choose(String... objects) {
        for (String t : objects) {
            if (t != null && !t.isEmpty()) return t;
        }
        throw new NullPointerException();
    }
}
