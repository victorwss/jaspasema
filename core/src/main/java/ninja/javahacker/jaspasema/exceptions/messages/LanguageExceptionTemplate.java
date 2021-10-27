package ninja.javahacker.jaspasema.exceptions.messages;

import java.util.Map;
import java.util.function.Function;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.badmapping.AllowedTypes;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class LanguageExceptionTemplate implements ExceptionTemplate {

    private final Map<Class<? extends Throwable>, Function<Throwable, String>> templates;
    private final Map<AllowedTypes, String> names;

    public LanguageExceptionTemplate(
            Map<Class<? extends Throwable>, Function<Throwable, String>> templates,
            Map<AllowedTypes, String> names)
    {
        this.templates = templates;
        this.names = names;
    }

    @NonNull
    @Override
    public String templateFor(@NonNull Throwable problem) {
        for (Class<?> k = problem.getClass(); k != Object.class; k = k.getSuperclass()) {
            var s = templates.get(k);
            if (s != null) return s.apply(problem);
        }
        return "";
    }

    public static Function<Throwable, String> template(@NonNull String template) {
        return problem -> {
            String filled = template;
            for (var m : problem.getClass().getMethods()) {
                String v;
                var t = m.getAnnotation(TemplateField.class);
                if (t != null) {
                    v = t.value();
                } else if (m.getName().equals("getCause") && m.getParameterCount() == 0) {
                    v = "CAUSE";
                } else if (m.getName().equals("getMessage") && m.getParameterCount() == 0) {
                    v = "MESSAGE";
                } else if (m.getName().equals("getClass") && m.getParameterCount() == 0) {
                    v = "CLASS";
                } else {
                    continue;
                }
                String replacement;
                try {
                    replacement = (String) m.invoke(problem);
                } catch (Throwable e) {
                    replacement = "<ERROR>";
                }
                filled = filled.replace("$" + v + "$", replacement);
            }
            return filled;
        };
    }

    @NonNull
    @Override
    public String nameFor(@NonNull AllowedTypes type) {
        var s = names.get(type);
        if (s == null) throw new AssertionError();
        return s;
    }
}
