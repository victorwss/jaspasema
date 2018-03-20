package ninja.javahacker.jaspasema.processor;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class JsonTypesProcessor {

    private static final TypeReference<HashMap<String, Object>> MAP_TYPE = new TypeReference<HashMap<String, Object>>() {};

    private static final ObjectMapper LENIENT = new ObjectMapper()
            .findAndRegisterModules()
            .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final ObjectMapper STRICT = new ObjectMapper()
            .findAndRegisterModules()
            .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

    private JsonTypesProcessor() {
        throw new UnsupportedOperationException();
    }

    public static <E> E convert(
            boolean lenient,
            Object obj,
            @NonNull TargetType<E> target)
    {
        return obj == null ? null : (lenient ? LENIENT : STRICT).convertValue(obj, target.getJavaType());
    }

    @SuppressWarnings("unchecked")
    public static <E, X extends Throwable> E readJson(
            boolean lenient,
            @NonNull Function<? super IOException, X> onError,
            @NonNull TargetType<E> jt,
            String data)
            throws X
    {
        if (data == null || data.isEmpty()) return null;
        if (jt.getGeneric() == String.class) return (E) data;

        try {
            return (lenient ? LENIENT : STRICT).readValue(data, jt.getJavaType());
        } catch (IOException e) {
            throw onError.apply(e);
        }
    }

    public static Map<String, Object> readJsonMap(
            @NonNull Parameter p,
            String data)
            throws MalformedParameterException
    {
        if (data == null || data.isEmpty()) return Collections.emptyMap();

        try {
            return STRICT.readValue(data, MAP_TYPE);
        } catch (IOException e) {
            throw new MalformedParameterException(p, "The body request data failed to be parseable as JSON.", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <E, X extends Throwable> String writeJson(
            @NonNull Function<? super IOException, X> onError,
            E value)
            throws X
    {
        try {
            return STRICT.writeValueAsString(value);
        } catch (IOException e) {
            throw onError.apply(e);
        }
    }
}
