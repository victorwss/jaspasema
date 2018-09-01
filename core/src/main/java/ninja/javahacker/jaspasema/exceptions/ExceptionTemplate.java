package ninja.javahacker.jaspasema.exceptions;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import lombok.Value;
import ninja.javahacker.jaspasema.exceptions.badmapping.TypeRestrictionViolationException;
import ninja.javahacker.jaspasema.processor.JsonTypesProcessor;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Value
public class ExceptionTemplate {

    Map<Class<? extends Throwable>, String> templates;
    Map<TypeRestrictionViolationException.AllowedTypes, String> alloweds;
    String rm;
    String p;

    public ExceptionTemplate(
            @NonNull Map<Class<? extends Throwable>, String> templates,
            @NonNull Map<TypeRestrictionViolationException.AllowedTypes, String> alloweds,
            @NonNull String rm,
            @NonNull String p)
    {
        Map<Class<? extends Throwable>, String> copy = Collections.unmodifiableMap(new HashMap<>(templates));
        this.alloweds = Collections.unmodifiableMap(new HashMap<>(alloweds));
        this.templates = copy;
        this.rm = rm;
        this.p = p;
        if (!copy.containsKey(Throwable.class)) throw new IllegalArgumentException();
    }

    public String getTemplateFor(@NonNull Class<? extends Throwable> type) {
        for (Class<?> k = type; k != Object.class; k = k.getSuperclass()) {
            String x = templates.get(k.asSubclass(Throwable.class));
            if (x != null) return x;
        }
        throw new AssertionError();
    }

    @Value
    private static class SubTemplate {
        Map<String, String> templates;
        Map<String, String> alloweds;
        String rm;
        String p;
    }

    public static ExceptionTemplate forJsonMap(String data) {
        SubTemplate s =
                JsonTypesProcessor.readJson(false, ReifiedGeneric.forClass(SubTemplate.class), data, IllegalArgumentException::new);

        Map<Class<? extends Throwable>, String> newMap = new HashMap<>(s.templates.size());
        for (Map.Entry<String, String> entry : s.templates.entrySet()) {
            try {
                newMap.put(Class.forName(entry.getKey()).asSubclass(Throwable.class), entry.getValue());
            } catch (ClassNotFoundException | ClassCastException x) {
                throw new IllegalArgumentException(x);
            }
        }

        Map<TypeRestrictionViolationException.AllowedTypes, String> newMap2 =
                new EnumMap<>(TypeRestrictionViolationException.AllowedTypes.class);
        for (Map.Entry<String, String> entry : s.alloweds.entrySet()) {
            try {
                newMap2.put(TypeRestrictionViolationException.AllowedTypes.valueOf(entry.getKey()), entry.getValue());
            } catch (ClassCastException x) {
                throw new IllegalArgumentException(x);
            }
        }

        return new ExceptionTemplate(newMap, newMap2, s.rm, s.p);
    }

    public static ExceptionTemplate forJsonMap(InputStream stream) throws IOException {
        return forJsonMap(new String(stream.readAllBytes(), StandardCharsets.UTF_8));
    }

    public static void setExceptionTemplate(@NonNull ExceptionTemplate template) {
        DefaultExceptionTemplate.ACTIVE.set(template);
    }

    public static ExceptionTemplate getExceptionTemplate() {
        return DefaultExceptionTemplate.ACTIVE.get();
    }

    public static String getFor(@NonNull Throwable t) {
        return getExceptionTemplate().getTemplateFor(t.getClass());
    }
}