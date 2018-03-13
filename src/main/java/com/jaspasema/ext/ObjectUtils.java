package br.gov.sp.prefeitura.smit.cgtic.jaspasema.ext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import lombok.experimental.UtilityClass;

/**
 * @author Victor Williams Stafusa da Silva
 */
@UtilityClass
public class ObjectUtils {
    @SafeVarargs
    public <T> T coallesce(T... objects) {
        for (T t : objects) {
            if (t != null) return t;
        }
        return null;
    }

    public String choose(String... objects) {
        for (String t : objects) {
            if (t != null && !t.isEmpty()) return t;
        }
        return null;
    }

    public <A, B> Map<A, B> makeMap(Consumer<BiConsumer<A, B>> work) {
        Map<A, B> map = new HashMap<>();
        work.accept(map::put);
        return Collections.unmodifiableMap(map);
    }
}
