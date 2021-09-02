package ninja.javahacker.jaspasema.processor;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
@UtilityClass
public class JsonTypesProcessor {

    private static final TypeReference<HashMap<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private static final ObjectMapper LENIENT = new ObjectMapper()
            .findAndRegisterModules()
            .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .configure(DeserializationFeature.FAIL_ON_TRAILING_TOKENS, true)
            .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final ObjectMapper STRICT = new ObjectMapper()
            .findAndRegisterModules()
            .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .configure(DeserializationFeature.FAIL_ON_TRAILING_TOKENS, true)
            .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

    @NonNull
    private static JavaType of(@NonNull ReifiedGeneric<?> t) {
        return TypeFactory.defaultInstance().constructType(t.getType());
    }

    @Nullable
    public <E> E convert(
            boolean lenient,
            @Nullable Object obj,
            @NonNull ReifiedGeneric<E> target)
    {
        return obj == null ? null : (lenient ? LENIENT : STRICT).convertValue(obj, of(target));
    }

    @Nullable
    @SuppressWarnings("unchecked")
    @SuppressFBWarnings({"LEST_LOST_EXCEPTION_STACK_TRACE", "URV_UNRELATED_RETURN_VALUES"})
    public <E, X extends Throwable> E readJson(
            boolean lenient,
            @NonNull ReifiedGeneric<E> jt,
            @Nullable String data,
            @NonNull Function<? super IOException, X> onError)
            throws X
    {
        if (data == null || data.isEmpty()) return null;
        if (jt.getType() == String.class) return (E) data;

        try {
            return (lenient ? LENIENT : STRICT).readValue(data, of(jt));
        } catch (JsonProcessingException e) {
            throw onError.apply(e);
        }
    }

    @NonNull
    @SuppressFBWarnings("LEST_LOST_EXCEPTION_STACK_TRACE")
    public <X extends Throwable> Map<String, Object> readJsonMap(
            @Nullable String data,
            @NonNull Function<? super IOException, X> onError)
            throws X
    {
        if (data == null || data.isEmpty()) return Collections.emptyMap();

        try {
            return STRICT.readValue(data, MAP_TYPE);
        } catch (JsonProcessingException e) {
            throw onError.apply(e);
        }
    }

    @Nullable
    public <E> String writeJson(@Nullable E value) throws JsonProcessingException {
        return STRICT.writeValueAsString(value);
    }
}
